package com.miu.bookhub.inventory.service;

import com.miu.bookhub.TestConfig;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.inventory.repository.BookItemRepository;
import com.miu.bookhub.inventory.repository.BookRepository;
import com.miu.bookhub.inventory.repository.entity.*;
import com.miu.bookhub.inventory.service.integration.BookSearchIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
public class InventoryServiceTest {

    private static final long BOOK_ID = 200;
    private static final long BOOK_ITEM_ID = 300;
    private static final String ISBN = "0553573403";
    private static final String ISNI = "0000000077784510";

    @MockBean private BookRepository bookRepository;
    @MockBean private BookItemRepository bookItemRepository;
    @MockBean private RegistrationService registrationService;
    @MockBean private BookSearchIntegrationService bookSearchIntegrationService;

    private InventoryService inventoryService;

    @BeforeEach
    void setup () {

        inventoryService = new InventoryServiceImpl(bookRepository,
                bookItemRepository, registrationService, bookSearchIntegrationService);
    }

    @Test
    void shouldFindBookByIsbn () {

        String isbn = "0553573403";

        when(bookRepository.findByIsbn(eq(isbn)))
                .thenReturn(Optional.of(getMockedBook()));

        Optional<Book> book = inventoryService.findBookByIsbn(isbn);

        assertThat(book)
                .as("Expected to find book by isbn")
                .isNotEmpty();
    }

    @Test
    void shouldRemoteSearchBookByIsbn() {

        String isbn = "0553573403";

        when(bookSearchIntegrationService.searchBookByIsbn(eq(isbn)))
                .thenReturn(Optional.of(getMockedBook()));

        Optional<Book> book = inventoryService.remoteSearchBookByIsbn(isbn);

        assertThat(book)
                .as("Expected to find book by isbn")
                .isNotEmpty();
    }

    @Test
    void shouldSearchBookByTitle() {

        String title = "A Game of";

        Book book = getMockedBook();
        when(bookRepository.findAllByTitleContainingIgnoreCase(contains(title), any()))
                .thenReturn(List.of(book));

        List<Book> books = inventoryService.searchBooksByTitle(title, Pageable.unpaged());

        var argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(bookRepository).findAllByTitleContainingIgnoreCase(anyString(), argumentCaptor.capture());

        assertThat(books)
                .as("Expected to find at least one book with title %s", book.getTitle())
                .extracting(Book::getTitle)
                .contains(book.getTitle());

        int defaultPageSize = 50;
        assertThat(argumentCaptor.getValue().getPageSize())
                .as("Expected the default page size of %d is applied", defaultPageSize)
                .isEqualTo(defaultPageSize);
    }

    @Test
    void shouldSearchBookByAuthor() {

        String author = "R.R. Martin";
        Book book = getMockedBook();

        when(bookRepository.findAllByAuthorsNameContainingIgnoreCase(contains(author), any()))
                .thenReturn(List.of(book));

        List<Book> books = inventoryService.searchBooksByAuthor(author, Pageable.unpaged());

        var argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(bookRepository).findAllByAuthorsNameContainingIgnoreCase(anyString(), argumentCaptor.capture());

        String authorName = book.getAuthors().get(0).getName();
        assertThat(books)
                .as("Expected to find at least one book with author %s", authorName)
                .flatExtracting(Book::getAuthors)
                .extracting(Author::getName)
                .contains(authorName);

        int defaultPageSize = 50;
        assertThat(argumentCaptor.getValue().getPageSize())
                .as("Expected the default page size of %d is applied", defaultPageSize)
                .isEqualTo(defaultPageSize);
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldSaveBookItem() {

        BookItem bookItem = getMockedBookItem();
        int prevQt = bookItem.getQuantity();

        when(registrationService.findUserById(anyLong()))
                .thenReturn(Optional.of(getMockedSeller()));

        when(bookRepository.findByIsbn(eq(ISBN)))
                .thenReturn(Optional.of(getMockedBook()));

        when(bookItemRepository.findByBookAndConditionAndSeller(any(Book.class), any(Condition.class), any(User.class)))
                .thenReturn(Optional.of(bookItem));

        when(bookItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookItem));

        when(bookItemRepository.save(any(BookItem.class)))
                .then(inv -> inv.getArgument(0, BookItem.class));

        Format format = Format.MASS_MARKET_PAPER_BACK;
        Condition condition = Condition.GOOD;
        int quantity = 2;
        double unitPrice = 35.0;

        BookItem savedBookItem = inventoryService.saveBookItem(TestConfig.TEST_USER_ID, ISBN, format, condition, quantity, unitPrice);

        assertThat(savedBookItem.getQuantity())
                .as("Expected item quantity to be %d", prevQt+ quantity)
                .isEqualTo(prevQt + quantity);
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldStockBookItem() {

        BookItem bookItem = getMockedBookItem();
        int prevQt = bookItem.getQuantity();
        int quantity = 2;

        when(bookItemRepository.findById(eq(BOOK_ITEM_ID)))
                .thenReturn(Optional.of(getMockedBookItem()));

        when(bookItemRepository.save(any(BookItem.class)))
                .then(inv -> inv.getArgument(0));

        BookItem updateBookItem = inventoryService.stockBookItem(bookItem.getId(), quantity);

        assertThat(updateBookItem.getQuantity())
                .as("Expected book item stock to increase to %d", prevQt + quantity)
                .isEqualTo(prevQt + quantity);
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldDeStockBookItem() {

        BookItem bookItem = getMockedBookItem();
        int quantity = 1;
        int prevQt = bookItem.getQuantity();

        when(bookItemRepository.findById(eq(BOOK_ITEM_ID)))
                .thenReturn(Optional.of(getMockedBookItem()));

        when(bookItemRepository.save(any(BookItem.class)))
                .then(inv -> inv.getArgument(0));

        BookItem updateBookItem = inventoryService.deStockBookItem(bookItem.getId(), quantity);

        assertThat(updateBookItem.getQuantity())
                .as("Expected book item stock to decrease to %d", prevQt - quantity)
                .isEqualTo(Math.max(0, prevQt - quantity));
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
                .id(BOOK_ID)
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
                .id(BOOK_ITEM_ID)
                .book(getMockedBook())
                .seller(getMockedSeller())
                .condition(Condition.GOOD)
                .quantity(3)
                .unitPrice(35.0)
                .build();
    }
}
