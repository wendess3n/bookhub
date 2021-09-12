package com.miu.bookhub.inventory.service;

import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.BookItem;
import com.miu.bookhub.inventory.repository.entity.Condition;
import com.miu.bookhub.inventory.repository.entity.Format;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    Optional<Book> findBookByIsbn(String isbn);

    Optional<Book> findBookById(long id);

    Optional<Book> remoteSearchBookByIsbn(String isbn);

    List<Book> searchBooksByTitle(String title, Pageable pageable);

    List<Book> searchBooksByAuthor(String author, Pageable pageable);

    BookItem saveBookItem(long sellerId, String isbn, Format format, Condition condition, int quantity, double unitPrice);

    Optional<BookItem> getBookItem(long bookItemId);

    BookItem stockBookItem(long bookItemId, int quantity);

    BookItem deStockBookItem(long bookItemId, int quantity);

    BookItem holdBookItem(long bookItemId, int quantity);

    BookItem unHoldBookItem(long bookItemId, int quantity);
}
