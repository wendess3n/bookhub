package com.miu.bookhub.order.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miu.bookhub.TestConfig;
import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.global.GlobalConfig;
import com.miu.bookhub.global.security.SecurityConfig;
import com.miu.bookhub.order.api.domain.OrderRequest;
import com.miu.bookhub.order.repository.entity.DeliveryStatus;
import com.miu.bookhub.order.repository.entity.PaymentStatus;
import com.miu.bookhub.order.service.FeeDto;
import com.miu.bookhub.order.service.OrderItemDto;
import com.miu.bookhub.order.service.OrderResult;
import com.miu.bookhub.order.service.OrderService;
import com.miu.bookhub.order.service.pricing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import({TestConfig.class, GlobalConfig.class, SecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    private static final String ORDER_REFERENCE_ID = "383e6024-2bd3-4aa0-9d20-95564de4111b";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private OrderService orderService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .apply(springSecurity())
                .build();
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldOrderBook() throws Exception {

        when(orderService.orderBook(anyLong(), anyList(), anyLong(), anyString()))
                .thenReturn(getMockedOrderResult());

        OrderRequest request = OrderRequest.builder()
                .orderItems(List.of(
                        new OrderItemDto(100L, 1),
                        new OrderItemDto(100L, 1)
                ))
                .shippingAddressId(200L)
                .remarks("For school")
                .build();

        mockMvc.perform(post("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("order-book",
                        requestFields(
                                fieldWithPath("shippingAddressId").description("Shipping address id from the list of saved addresses"),
                                fieldWithPath("remarks").description("Any remarks that can be attached to the order"),
                                fieldWithPath("orderItems[].bookItemId").description("Id of the book item to buy"),
                                fieldWithPath("orderItems[].quantity").description("No of books to buy for this item")
                        ),
                        getOrderResponseFields()
                ));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    void shouldCancelOrder() throws Exception {

        mockMvc.perform(post("/orders/{referenceId}/cancel", ORDER_REFERENCE_ID))
                .andExpect(status().isOk())
                .andDo(document("cancel-order"));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    void shouldFindOrderByReference() throws Exception {

        when(orderService.findOrderByReference(anyString()))
                .thenReturn(Optional.of(getMockedOrderResult()));

        mockMvc.perform(get("/orders/{referenceId}", ORDER_REFERENCE_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-order-by-reference-id",
                        getOrderResponseFields()
                ));
    }

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    void shouldFindOrdersOfACustomer() throws Exception {

        when(orderService.findOrders(any(User.class), any()))
                .thenReturn(List.of(getMockedOrderResult()));

        mockMvc.perform(get("/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-orders",
                        responseFields(
                                fieldWithPath("[].referenceId").description("Unique reference for the transaction"),
                                fieldWithPath("[].price").description("Base price"),
                                fieldWithPath("[].fees[].type").description("Applied fee type"),
                                fieldWithPath("[].fees[].amount").description("Applied fee value"),
                                fieldWithPath("[].totalPrice").description("Total price a customer has to pay"),
                                fieldWithPath("[].customerId").description("Id of the customer purchasing the book"),
                                fieldWithPath("[].shippingAddressId").description("Id of the shipping address"),
                                fieldWithPath("[].orderDate").description("Time of the order"),
                                fieldWithPath("[].paymentStatus").description("Payment status for the order"),
                                fieldWithPath("[].shippingStatus").description("Package shipping status")
                        )
                ));
    }

    private ResponseFieldsSnippet getOrderResponseFields() {

        return responseFields(
                fieldWithPath("referenceId").description("Unique reference for the transaction"),
                fieldWithPath("price").description("Base price"),
                fieldWithPath("fees[].type").description("Applied fee type"),
                fieldWithPath("fees[].amount").description("Applied fee value"),
                fieldWithPath("totalPrice").description("Total price a customer has to pay"),
                fieldWithPath("customerId").description("Id of the customer purchasing the book"),
                fieldWithPath("shippingAddressId").description("Id of the shipping address"),
                fieldWithPath("orderDate").description("Time of the order"),
                fieldWithPath("paymentStatus").description("Payment status for the order"),
                fieldWithPath("shippingStatus").description("Package shipping status")
        );
    }

    private OrderResult getMockedOrderResult() {

        Fee convenienceFee =  new ConvenienceFee(0.3);
        Fee shippingFee = new ShippingFee(null, null, .5);
        Fee vat = new Vat(.015);

        double baseAmount = 100;
        double fees = convenienceFee.computeAmount(baseAmount)
                + shippingFee.computeAmount(baseAmount)
                + vat.computeAmount(baseAmount);

        User customer = getMockedCustomer();

        return OrderResult.builder()
                .referenceId(ORDER_REFERENCE_ID)
                .price(fees)
                .fees(List.of(
                        new FeeDto(FeeType.CONVENIENCE_FEE, convenienceFee.computeAmount(baseAmount)),
                        new FeeDto(FeeType.SHIPPING_FEE, shippingFee.computeAmount(baseAmount)),
                        new FeeDto(FeeType.VAT, vat.computeAmount(baseAmount))
                ))
                .totalPrice(baseAmount + fees)
                .customer(customer)
                .shippingAddress(getMockShippingAddress(customer))
                .shippingStatus(DeliveryStatus.PENDING)
                .orderDate(LocalDateTime.now().minusSeconds(1))
                .paymentStatus(PaymentStatus.PENDING)
                .shippingStatus(DeliveryStatus.PENDING)
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
}
