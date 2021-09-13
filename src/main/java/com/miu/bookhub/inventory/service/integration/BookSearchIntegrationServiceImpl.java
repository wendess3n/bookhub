package com.miu.bookhub.inventory.service.integration;

import com.miu.bookhub.inventory.repository.entity.Book;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class BookSearchIntegrationServiceImpl implements BookSearchIntegrationService {

    private final BookSearchIntegrationService bookSearchIntegrationService;

    public BookSearchIntegrationServiceImpl(@Qualifier("openLibBookSearchingService")
                                                    BookSearchIntegrationService bookSearchIntegrationService) {
        this.bookSearchIntegrationService = bookSearchIntegrationService;
    }

    @Override
    public Optional<Book> searchBookByIsbn(String isbn) {
        return bookSearchIntegrationService.searchBookByIsbn(isbn);
    }
}
