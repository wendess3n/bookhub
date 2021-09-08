package com.miu.bookhub.inventory.service.integration;

import com.miu.bookhub.inventory.repository.entity.Book;

import java.util.Optional;

public interface BookSearchIntegrationService {

    Optional<Book> searchBookByIsbn(String isbn);
}
