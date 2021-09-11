package com.miu.bookhub.inventory.api;

import com.miu.bookhub.TestConfig;
import com.miu.bookhub.global.GlobalConfig;
import com.miu.bookhub.inventory.repository.entity.Author;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.Format;
import com.miu.bookhub.inventory.service.InventoryService;
import com.miu.bookhub.security.SecurityConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import({TestConfig.class, GlobalConfig.class, SecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    private static final String ISBN = "0553573403";
    private static final String ISNI = "0000000077784510";

    @MockBean private InventoryService inventoryService;
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

    @WithMockUser
    @Test
    void shouldFindBookByIsbn() throws Exception {


        when(inventoryService.findBookByIsbn(eq(ISBN)))
                .thenReturn(Optional.of(getMockedBook()));

        mockMvc.perform(get("/books?isbn={isbn}", ISBN).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-book-by-isbn",
                        getBookResponseFields()
                ));
    }

    @WithMockUser
    @Test
    void shouldSearchBookByTitle() throws Exception {

        String title = "A Game of";
        int page = 1;
        int size = 20;

        when(inventoryService.searchBooksByTitle(contains(title), any()))
                .thenReturn(List.of(getMockedBook()));

        mockMvc.perform(get("/books?title={title}&page={page}&size={size}", title, page, size))
                .andExpect(status().isOk())
                .andDo(document("search-book-by-title",
                        getBookResponseFieldsSet()
                ));

        var argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(inventoryService).searchBooksByTitle(anyString(), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getPageNumber())
                .as("Expected service to be given a pagination of page %d", page)
                .isEqualTo(page);

        assertThat(argumentCaptor.getValue().getPageSize())
                .as("Expected service to be given a pagination of size %d", size)
                .isEqualTo(size);
    }

    @WithMockUser
    @Test
    void shouldSearchBookByAuthor() throws Exception {

        String author = "R.R. Martin";
        int page = 2;
        int size = 5;

        when(inventoryService.searchBooksByAuthor(contains(author), any()))
                .thenReturn(List.of(getMockedBook()));

        mockMvc.perform(get("/books?author={author}&page={page}&size={size}", author, page, size))
                .andExpect(status().isOk())
                .andDo(document("search-book-by-author",
                        getBookResponseFieldsSet()
                ));

        var argumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(inventoryService).searchBooksByAuthor(anyString(), argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getPageNumber())
                .as("Expected service to be given a pagination of page %d", page)
                .isEqualTo(page);

        assertThat(argumentCaptor.getValue().getPageSize())
                .as("Expected service to be given a pagination of size %d", size)
                .isEqualTo(size);
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

    private ResponseFieldsSnippet getBookResponseFields() {

        return responseFields(
                fieldWithPath("bookId").description("Unique system ID to refer a book"),
                fieldWithPath("isbn").description("International Standard Book Number"),
                fieldWithPath("title").description("Title of the book"),
                fieldWithPath("edition").description("Edition of the book"),
                fieldWithPath("publisher").description("Name of the book publisher"),
                fieldWithPath("publishDate").description("Year and month of the publication date"),
                fieldWithPath("format").description("Book format. Possible values are " + StringUtils.join(Format.values(), ", ")),
                fieldWithPath("pageCount").description("No of pages in the book in the given format"),
                fieldWithPath("weight").description("Weight of the book in pounds"),
                fieldWithPath("authors[].name").description("Publicly known name of the author"),
                fieldWithPath("authors[].isni").description("International Standard Name Identifier"),
                fieldWithPath("authors[].photoUri").description("Uri of the author's photo")
        );
    }

    private ResponseFieldsSnippet getBookResponseFieldsSet() {

        return responseFields(
                fieldWithPath("[].bookId").description("Unique system ID to refer a book"),
                fieldWithPath("[].isbn").description("International Standard Book Number"),
                fieldWithPath("[].title").description("Title of the book"),
                fieldWithPath("[].edition").description("Edition of the book"),
                fieldWithPath("[].publisher").description("Name of the book publisher"),
                fieldWithPath("[].publishDate").description("Year and month of the publication date"),
                fieldWithPath("[].format").description("Book format. Possible values are " + StringUtils.join(Format.values(), ", ")),
                fieldWithPath("[].pageCount").description("No of pages in the book in the given format"),
                fieldWithPath("[].weight").description("Weight of the book in pounds"),
                fieldWithPath("[].authors[].name").description("Publicly known name of the author"),
                fieldWithPath("[].authors[].isni").description("International Standard Name Identifier"),
                fieldWithPath("[].authors[].photoUri").description("Uri of the author's photo")
        );
    }
}
