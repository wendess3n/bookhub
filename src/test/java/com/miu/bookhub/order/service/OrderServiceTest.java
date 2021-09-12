package com.miu.bookhub.order.service;

import com.miu.bookhub.TestConfig;
import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.inventory.repository.entity.*;
import com.miu.bookhub.inventory.service.InventoryService;
import com.miu.bookhub.order.repository.OrderItemRepository;
import com.miu.bookhub.order.repository.OrderRepository;
import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.Order;
import com.miu.bookhub.order.repository.entity.OrderItem;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import com.miu.bookhub.order.service.pricing.ConvenienceFee;
import com.miu.bookhub.order.service.pricing.FeeType;
import com.miu.bookhub.order.service.pricing.ShippingFee;
import com.miu.bookhub.order.service.pricing.Vat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @MockBean private OrderRepository orderRepository;
    @MockBean private InventoryService inventoryService;
    @MockBean private OrderItemRepository orderItemRepository;
    @MockBean private RegistrationService registrationService;

    @BeforeEach
    void setup() {
        orderService = new OrderServiceImpl(orderRepository, inventoryService, orderItemRepository, registrationService);
    }

    @Test
    void shouldOrderBook() {

        /*
        Given
         */
        BookItem bookItem = getMockedBookItem();
        User customer = getMockedCustomer();
        Address shippingAddress = getMockShippingAddress(customer);
        customer.setAddresses(List.of(shippingAddress));
        int quantity = 2;

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bookItem.getId(), quantity));

        /*
        When
         */
        when(registrationService.findUserById(eq(customer.getId())))
                .thenReturn(Optional.of(customer));

        when(orderRepository.save(any(Order.class)))
                .then(inv -> {
                    Order order = inv.getArgument(0);
                    order.setId(300L);
                    return order;
                });

        when(orderItemRepository.save(any(OrderItem.class)))
                .then(inv -> {
                    OrderItem orderItem = inv.getArgument(0);
                    orderItem.setId(400L);
                    return orderItem;
                });

        when(inventoryService.getBookItem(eq(bookItem.getId())))
                .thenReturn(Optional.of(bookItem));

        when(inventoryService.holdBookItem(eq(bookItem.getId()), anyInt()))
                .then(inv -> {
                    int qyt = inv.getArgument(1);
                    bookItem.setQuantity(bookItem.getQuantity() - qyt);
                    bookItem.setHeldQuantity(bookItem.getHeldQuantity() + qyt);
                    return bookItem;
                });

        OrderResult result = orderService.orderBook(customer.getId(), orderItems, shippingAddress.getId(), "for school");

        /*
        Assert
         */
        verify(inventoryService, times(1)).holdBookItem(eq(bookItem.getId()), anyInt());

        double baseAmount = bookItem.getUnitPrice() * quantity;
        double feesTotal = new ConvenienceFee(0.3).computeAmount(baseAmount)
                + new ShippingFee(null, shippingAddress, .5).computeAmount(baseAmount)
                + new Vat(.015).computeAmount(baseAmount);

        assertThat(result.getReferenceId())
                .as("Expected order result to have a reference id")
                .isNotNull();

        assertThat(result.getFees())
                .as("Expected convenience, vat and shipping fee to be applied")
                .asList()
                .extracting(fee -> ((FeeDto) fee).getType())
                .contains(FeeType.CONVENIENCE_FEE, FeeType.SHIPPING_FEE, FeeType.VAT);

        assertThat(result.getPrice())
                .as("Expected the base price to be %d", baseAmount)
                .isEqualTo(baseAmount);

        assertThat(result.getTotalPrice())
                .as("Expected total price to be %d", baseAmount + feesTotal)
                .isEqualTo(baseAmount + feesTotal);
    }

    @Test
    void shouldFindOrders() {

        when(orderRepository.findAllByCustomerOrderByOrderDateDesc(any(User.class), any()))
                .thenReturn(List.of());

        orderService.findOrders(getMockedCustomer(), Pageable.unpaged());

        var argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAllByCustomerOrderByOrderDateDesc(any(User.class), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getPageSize())
                .as("Expected the default page size is applied for un paged request")
                .isEqualTo(50);
    }

    @Test
    void shouldCheckoutOrder() {

        Order order = getMockedOrder();
        BookItem bookItem = getMockedBookItem();

        when(orderRepository.findOrderByReferenceId(anyString()))
                .thenReturn(Optional.of(order));

        when(inventoryService.unHoldBookItem(eq(bookItem.getId()), anyInt()))
                .then(inv -> {
                    int qyt = inv.getArgument(1);
                    bookItem.setQuantity(bookItem.getQuantity() + qyt);
                    bookItem.setHeldQuantity(bookItem.getHeldQuantity() - qyt);
                    return bookItem;
                });

        when(inventoryService.deStockBookItem(eq(bookItem.getId()), anyInt()))
                .then(inv -> {
                    int qyt = inv.getArgument(1);
                    bookItem.setQuantity(bookItem.getQuantity() - qyt);
                    return bookItem;
                });

        when(orderRepository.save(any(Order.class)))
                .then(inv -> inv.getArgument(0));

        orderService.checkoutOrder(order.getReferenceId());

        verify(inventoryService).unHoldBookItem(eq(bookItem.getId()), eq(2));
        verify(inventoryService).deStockBookItem(eq(bookItem.getId()), eq(2));
    }

    void shouldCancelOrder() {

        Order order = getMockedOrder();
        BookItem bookItem = getMockedBookItem();

        when(orderRepository.findOrderByReferenceId(anyString()))
                .thenReturn(Optional.of(order));

        when(inventoryService.unHoldBookItem(eq(bookItem.getId()), anyInt()))
                .then(inv -> {
                    int qyt = inv.getArgument(1);
                    bookItem.setQuantity(bookItem.getQuantity() + qyt);
                    bookItem.setHeldQuantity(bookItem.getHeldQuantity() - qyt);
                    return bookItem;
                });

        when(orderRepository.save(any(Order.class)))
                .then(inv -> inv.getArgument(0));

        orderService.cancelOrder(order.getReferenceId());

        verify(inventoryService).unHoldBookItem(eq(bookItem.getId()), 2);
    }

    private Address getMockShippingAddress(User customer) {

        return Address.builder()
                .id(100L)
                .country("United States")
                .state("Iowa")
                .city("Fairfield")
                .addressLine1("1000 N")
                .zipCode("52557")
                .isPrimary(true)
                .user(customer)
                .build();
    }

    private User getMockedCustomer() {

        return User.builder()
                .id(TestConfig.TEST_USER_ID)
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress("adam.abel@email.com")
                .isLocked(false)
                .isActive(true)
                .addresses(List.of())
                .roles(Set.of(Role.CUSTOMER))
                .build();
    }

    private User getMockedSeller() {

        return User.builder()
                .id(200L)
                .firstName("Jane")
                .lastName("Doe")
                .emailAddress("jane.doe@email.com")
                .isLocked(false)
                .isActive(true)
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .build();
    }

    private Book getMockedBook() {

        Author author = Author.builder()
                .name("George R.R. Martin")
                .personalName("George R.R. Martin")
                .isni("0000000077784510")
                .photoUri("https://covers.openlibrary.org/a/id/6155669-L.jpg")
                .build();

        return Book.builder()
                .id(100L)
                .isbn("0553573403")
                .title("A Game of Thrones")
                .publisher("Random House Publishing Group")
                .publishDate(LocalDate.of(1997, 8, 1))
                .pageCount(864)
                .weight(0.93)
                .format(Format.MASS_MARKET_PAPER_BACK)
                .authors(List.of(author))
                .build();
    }

    private BookItem getMockedBookItem() {

        return BookItem.builder()
                .id(200L)
                .book(getMockedBook())
                .seller(getMockedSeller())
                .condition(Condition.GOOD)
                .quantity(3)
                .unitPrice(35.0)
                .build();
    }

    private Order getMockedOrder() {

        return Order.builder()
                .id(100L)
                .orderDate(LocalDateTime.now().minusDays(1))
                .customer(getMockedCustomer())
                .deliveryStatus(DeliveryStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItems(getMockedOrderItems())
                .referenceId("54cb0bed-7f7b-42af-b4e4-e11bc8597db7")
                .build();
    }

    private List<OrderItem> getMockedOrderItems() {

        return List.of(OrderItem.builder()
                .id(250L)
                .bookItem(getMockedBookItem())
                .quantity(2)
                .build());
    }
}
