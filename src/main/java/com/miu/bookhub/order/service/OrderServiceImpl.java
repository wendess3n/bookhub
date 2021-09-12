package com.miu.bookhub.order.service;

import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.global.i18n.DefaultMessageSource;
import com.miu.bookhub.global.utils.SecurityUtils;
import com.miu.bookhub.inventory.repository.entity.BookItem;
import com.miu.bookhub.inventory.service.InventoryService;
import com.miu.bookhub.order.exception.OrderServiceException;
import com.miu.bookhub.order.repository.OrderItemRepository;
import com.miu.bookhub.order.repository.OrderRepository;
import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.Order;
import com.miu.bookhub.order.repository.entity.OrderItem;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import com.miu.bookhub.order.service.pricing.ConvenienceFee;
import com.miu.bookhub.order.service.pricing.Fee;
import com.miu.bookhub.order.service.pricing.ShippingFee;
import com.miu.bookhub.order.service.pricing.Vat;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private final static MessageSourceAccessor messages = DefaultMessageSource.getAccessor();

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final OrderItemRepository orderItemRepository;
    private final RegistrationService registrationService;

    @Transactional
    @Override
    public OrderResult orderBook(long customerId, List<OrderItemDto> orderItems, long addressId, String remarks) {

        User customer = registrationService.findUserById(customerId).orElse(null);
        Address deliveryAddress = validateOrderRequest(orderItems, addressId, customer);

        Order order = orderRepository.save(Order.builder()
                .customer(customer)
                .deliveryAddress(deliveryAddress)
                .paymentStatus(PaymentStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .deliveryStatus(DeliveryStatus.PENDING)
                .remarks(remarks)
                .build());

        var price = new AtomicReference<>(0.0);

        orderItems.forEach(orderItem -> {

            Optional<BookItem> bookItem = inventoryService.getBookItem(orderItem.getBookItemId());

            // Make sure book is on stock
            if (bookItem.isEmpty() || bookItem.get().getQuantity() == 0) {
                throw new OrderServiceException(messages.getMessage("order.status.notAvailable",
                        new String[]{bookItem.isEmpty() ? "" : bookItem.get().getBook().getTitle()}));
            }

            BookItem bkItem = bookItem.get();
            price.getAndSet(price.get() + (bkItem.getUnitPrice() * orderItem.getQuantity()));

            bkItem = inventoryService.holdBookItem(bkItem.getId(), orderItem.getQuantity());
            orderItemRepository.save(OrderItem.builder()
                    .bookItem(bkItem)
                    .quantity(orderItem.getQuantity())
                    .order(order)
                    .build());
        });

        double baseAmount = price.get();
        List<FeeDto> fees = getFee(baseAmount, deliveryAddress);
        double totalFee = fees.stream()
                .mapToDouble(FeeDto::getAmount)
                .sum();

        order.setAmount(baseAmount);
        order.setTotalFee(totalFee);
        orderRepository.save(order);

        return buildOrderResult(order, fees);
    }

    @Override
    public Optional<OrderResult> findOrderByReference(String referenceId) {

        return orderRepository.findOrderByReferenceId(referenceId)
                .map(order -> buildOrderResult(order, List.of()));
    }

    @Override
    public List<OrderResult> findOrders(User customer, Pageable pageable) {

        pageable = pageable == null || pageable.isUnpaged() ? Pageable.ofSize(DEFAULT_PAGE_SIZE) : pageable;

        return orderRepository.findAllByCustomerOrderByOrderDateDesc(customer, pageable).stream()
                .map(order -> buildOrderResult(order, List.of()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void checkoutOrder(String referenceId) {

        Order order = validateOrderId(referenceId);

        if (order.getPaymentStatus() != PaymentStatus.PENDING)
            throw new OrderServiceException(messages.getMessage("order.status.nonPending", new String[]{order.getPaymentStatus().toString().toLowerCase()}));

        // un hold and de stock item
        order.getOrderItems().forEach(item -> {

            int qty = item.getQuantity();
            inventoryService.unHoldBookItem(item.getBookItem().getId(), qty);
            inventoryService.deStockBookItem(item.getBookItem().getId(), qty);
        });

        order.setPaymentStatus(PaymentStatus.APPROVED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void cancelOrder(String referenceId) {

        Order order = validateOrderId(referenceId);

        SecurityUtils.validateAuthorizationOnResource(order.getCustomer().getId());

        if (order.getPaymentStatus() != PaymentStatus.PENDING)
            throw new OrderServiceException(messages.getMessage("order.cancel.prohibited", new String[]{order.getPaymentStatus().toString().toLowerCase()}));

        // un hold item to make it available for others
        order.getOrderItems().forEach(item -> {

            int qty = item.getQuantity();
            inventoryService.unHoldBookItem(item.getBookItem().getId(), qty);
        });

        order.setPaymentStatus(PaymentStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public void updateOrderDeliveryStatus(String referenceId, DeliveryStatus deliveryStatus) {

        Order order = validateOrderId(referenceId);
        order.setDeliveryStatus(deliveryStatus);
        orderRepository.save(order);
    }

    private Order validateOrderId(String referenceId) {

        Order order = orderRepository.findOrderByReferenceId(referenceId).orElse(null);
        if (order == null) throw new OrderServiceException(messages.getMessage("order.id.invalid"));

        return order;
    }

    private Address validateOrderRequest(List<OrderItemDto> orderItems, long addressId, User customer) {

        if (customer == null) throw new OrderServiceException(messages.getMessage("user.id.invalid"));

        if (customer.getAddresses().isEmpty()) {
            throw new OrderServiceException(messages.getMessage("user.address.invalid"));
        }

        Address deliveryAddress = customer.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> {
                    throw new OrderServiceException(messages.getMessage("user.address.invalid"));
                });

        if (orderItems == null || orderItems.isEmpty()) {
            throw new OrderServiceException(messages.getMessage("order.item.empty"));
        }

        return deliveryAddress;
    }

    private List<FeeDto> getFee(double baseAmount, Address shippingAddress) {

        List<Fee> fees = List.of(
                new ConvenienceFee(0.3),
                new ShippingFee(getStockAddress(), shippingAddress, .5),
                new Vat(.015)
        );

        return fees.stream()
                .map(fee -> new FeeDto(fee.type, fee.computeAmount(baseAmount)))
                .collect(Collectors.toList());
    }

    private OrderResult buildOrderResult(Order order, List<FeeDto> fees) {

        return OrderResult.builder()
                .referenceId(order.getReferenceId())
                .price(order.getAmount())
                .fees(fees)
                .totalPrice(order.getAmount() + order.getTotalFee())
                .customer(order.getCustomer())
                .shippingAddress(order.getDeliveryAddress())
                .orderDate(order.getOrderDate())
                .paymentStatus(order.getPaymentStatus())
                .shippingStatus(order.getDeliveryStatus())
                .build();
    }

    private Address getStockAddress() {
        return null;
    }
}
