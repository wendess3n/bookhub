package com.miu.bookhub.inventory.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miu.bookhub.TestConfig;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.global.GlobalConfig;
import com.miu.bookhub.inventory.api.domain.BookItemRequest;
import com.miu.bookhub.inventory.repository.entity.*;
import com.miu.bookhub.inventory.service.InventoryService;
import com.miu.bookhub.security.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
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

import java.time.LocalDate;
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
@Import({GlobalConfig.class, SecurityConfig.class, TestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InventoryController.class)
public class InventoryControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ISBN = "0553573403";
    private static final String ISNI = "0000000077784510";

    @MockBean
    private InventoryService inventoryService;
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
    void shouldPostBookItem() throws Exception {

        BookItemRequest request = BookItemRequest.builder()
                .isbn(ISBN)
                .format(Format.MASS_MARKET_PAPER_BACK)
                .condition(Condition.LIKE_NEW)
                .quantity(1)
                .unitPrice(35.0)
                .build();

        when(inventoryService.saveBookItem(anyLong(), anyString(), any(), any(), anyInt(), anyDouble()))
                        .thenReturn(getMockedBookItem());

        mockMvc.perform(post("/book-items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("post-book-item",
                        requestFields(
                                fieldWithPath("isbn").description("International Standard Book Number"),
                                fieldWithPath("format").description("Book format. Possible values are [" + StringUtils.join(Format.values(), ", ") + "]"),
                                fieldWithPath("condition").description("Condition of the book. Possible values are [" + StringUtils.join(Condition.values(), ", ") + "]"),
                                fieldWithPath("quantity").description("Quantity of the book item to post"),
                                fieldWithPath("unitPrice").description("Price of the single item")
                        ),
                        getBookItemResponseFields()
                ));
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldGetBookItemById() throws Exception {

        when(inventoryService.getBookItem(anyLong()))
                .thenReturn(Optional.of(getMockedBookItem()));

        mockMvc.perform(get("/book-items/1" ).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-book-item-by-id",
                        getBookItemResponseFields()
                ));
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldStockBookItem() throws Exception {

        when(inventoryService.stockBookItem(anyLong(), anyInt()))
                .thenReturn(getMockedBookItem());

        mockMvc.perform(post("/book-items/1/stock")
                                .content("3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("stock-book-item",
                        getBookItemResponseFields()
                ));
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldDStockBookItem() throws Exception {

        when(inventoryService.deStockBookItem(anyLong(), anyInt()))
                .thenReturn(getMockedBookItem());

        mockMvc.perform(post("/book-items/1/destock")
                                .content("1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("deStock-book-item",
                        getBookItemResponseFields()
                ));
    }

    private ResponseFieldsSnippet getBookItemResponseFields() {

        return responseFields(
                fieldWithPath("bookItemId").description("Unique Id for the newly posted book item"),
                fieldWithPath("bookId").description("Unique id for the book"),
                fieldWithPath("isbn").description("International Standard Book Number"),
                fieldWithPath("format").description("Book format. Possible values are [" + StringUtils.join(Format.values(), ", ") + "]"),
                fieldWithPath("condition").description("Condition of the book. Possible values are [" + StringUtils.join(Condition.values(), ", ") + "]"),
                fieldWithPath("quantity").description("The current stock of the book item"),
                fieldWithPath("unitPrice").description("Price of the single item")
        );
    }

    private User getMockedSeller() {

        return User.builder()
                .id(TestConfig.TEST_USER_ID)
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress("adam.abel@email.com")
                .isLocked(false)
                .isActive(true)
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .build();
    }

    private Book getMockedBook() {

        Author author = Author.builder()
                .name("George R.R. Martin")
                .personalName("George R.R. Martin")
                .isni(ISNI)
                .photoUri("https://covers.openlibrary.org/a/id/6155669-L.jpg")
                .build();

        return Book.builder()
                .id(100L)
                .isbn(ISBN)
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
}
