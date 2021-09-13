package com.miu.bookhub.inventory.service;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.global.i18n.DefaultMessageSource;
import com.miu.bookhub.global.utils.SecurityUtils;
import com.miu.bookhub.inventory.exception.InventoryExceptionService;
import com.miu.bookhub.inventory.repository.BookItemRepository;
import com.miu.bookhub.inventory.repository.BookRepository;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.BookItem;
import com.miu.bookhub.inventory.repository.entity.Condition;
import com.miu.bookhub.inventory.service.integration.BookSearchIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {

    private final static MessageSourceAccessor messages = DefaultMessageSource.getAccessor();
    private final int DEFAULT_BOOK_SEARCH_SIZE = 50;

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final RegistrationService registrationService;
    private final BookSearchIntegrationService bookSearchIntegrationService;

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Optional<Book> findBookById(long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Optional<Book> remoteSearchBookByIsbn(String isbn) {

        Optional<Book> book = bookSearchIntegrationService.searchBookByIsbn(isbn);

        try { // this axillary block should not affect the main process; hence the try-catch

            if (book.isPresent() && bookRepository.findByIsbn(isbn).isEmpty()) {
                bookRepository.save(book.get());
            }
        } catch (Exception ignore) {}

        return book;
    }

    @Override
    public List<Book> searchBooksByTitle(String title, Pageable pageable) {

        pageable = pageable == null || pageable.isUnpaged() ? Pageable.ofSize(DEFAULT_BOOK_SEARCH_SIZE) : pageable;
        return bookRepository.findAllByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public List<Book> searchBooksByAuthor(String author, Pageable pageable) {

        pageable = pageable == null || pageable.isUnpaged() ? Pageable.ofSize(DEFAULT_BOOK_SEARCH_SIZE) : pageable;
        return bookRepository.findAllByAuthorsNameContainingIgnoreCase(author, pageable);
    }

    @Override
    public BookItem saveBookItem(long sellerId, String isbn, Condition condition, int quantity, double unitPrice) {

        if (!isIsbnValid(isbn)) throw new InventoryExceptionService(messages.getMessage("book.isbn.invalid"));

        if (condition == null) throw new InventoryExceptionService(messages.getMessage("book.condition.blank"));

        User seller = registrationService.findUserById(sellerId)
                .orElseThrow(() -> new InventoryExceptionService(messages.getMessage("user.id.invalid")));

        Optional<Book> book = findBookByIsbn(isbn) // If not found from local cache, query from remote external store
                .or(() -> remoteSearchBookByIsbn(isbn));

        if (book.isEmpty()) throw new InventoryExceptionService(messages.getMessage("book.isbn.invalid"));

        Optional<BookItem> bookItem = bookItemRepository.findByBookAndConditionAndSeller(book.get(), condition, seller);

        if (bookItem.isPresent()) {
            return stockBookItem(bookItem.get().getId(), quantity);
        } else {

            BookItem bkItem = BookItem.builder()
                    .book(book.get())
                    .seller(seller)
                    .condition(condition)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();

            return bookItemRepository.save(bkItem);
        }
    }

    @Override
    public Optional<BookItem> getBookItem(long bookItemId) {
        return bookItemRepository.findById(bookItemId);
    }

    @Override
    public BookItem stockBookItem(long bookItemId, int quantity) {

        return bookItemRepository.findById(bookItemId)
                .map(bookItem -> {

                    SecurityUtils.validateAuthorizationOnResource(bookItem.getSeller().getId());

                    bookItem.setQuantity(bookItem.getQuantity() + quantity);
                    return bookItemRepository.save(bookItem);
                })
                .orElseThrow(() -> new InventoryExceptionService(messages.getMessage("book.bookItemId.invalid")));
    }

    @Override
    public BookItem deStockBookItem(long bookItemId, int quantity) {

        return bookItemRepository.findById(bookItemId)
                .map(bookItem -> {

                    SecurityUtils.validateAuthorizationOnResource(bookItem.getSeller().getId());

                    bookItem.setQuantity(Math.max(0, bookItem.getQuantity() - quantity));
                    return bookItemRepository.save(bookItem);
                })
                .orElseThrow(() -> new InventoryExceptionService(messages.getMessage("book.bookItemId.invalid")));
    }

    @Override
    public BookItem holdBookItem(long bookItemId, int quantity) {

        BookItem bookItem = bookItemRepository.findById(bookItemId).orElse(null);
        if (bookItem == null || bookItem.getQuantity() < quantity) {
            throw new InventoryExceptionService(messages.getMessage("bookItem.stock.low"));
        }

        bookItem.setQuantity(bookItem.getQuantity() - quantity);
        bookItem.setHeldQuantity(bookItem.getHeldQuantity() + quantity);

        return bookItemRepository.save(bookItem);
    }

    @Transactional
    @Override
    public BookItem unHoldBookItem(long bookItemId, int quantity) {

        BookItem bookItem = bookItemRepository.findById(bookItemId).orElse(null);
        if (bookItem == null || bookItem.getHeldQuantity() < quantity) {
            throw new InventoryExceptionService(messages.getMessage("bookItem.heldStock.low"));
        }

        bookItem.setQuantity(bookItem.getQuantity() + quantity);
        bookItem.setHeldQuantity(bookItem.getHeldQuantity() - quantity);

        return bookItemRepository.save(bookItem);
    }

    // Formula borrowed from the internet
    private boolean isIsbnValid(String isbn) {

        // length must be 10
        int n = isbn.length();
        if (n != 10)
            return false;

        // Computing weighted sum
        // of first 9 digits
        int sum = 0;
        for (int i = 0; i < 9; i++)
        {
            int digit = isbn.charAt(i) - '0';
            if (0 > digit || 9 < digit)
                return false;
            sum += (digit * (10 - i));
        }

        // Checking last digit.
        char last = isbn.charAt(9);
        if (last != 'X' && (last < '0' ||
                last > '9'))
            return false;

        // If last digit is 'X', add 10
        // to sum, else add its value
        sum += ((last == 'X') ? 10 : (last - '0'));

        // Return true if weighted sum
        // of digits is divisible by 11.
        return (sum % 11 == 0);
    }
}
