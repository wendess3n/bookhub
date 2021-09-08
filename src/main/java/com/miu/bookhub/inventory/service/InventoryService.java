package com.miu.bookhub.inventory.service;

import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.Condition;
import com.miu.bookhub.inventory.repository.entity.Format;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface InventoryService {

    BookItem saveBookItem(int sellerId, String isbn, Format format, Condition condition, int quantity, double unitPrice);

    BookItem getBookItem(long bookId);

    Book searchBookByIsbn(String isbn);

    Set<Book> searchBooksByTitle(String title, Pageable pageable);

    Set<Book> searchBooksByAuthor(String author, Pageable pageable);

    void stockBookItem(long bookId, int quantity);

    void deStockBookItem(long bookId, int quantity);
}
