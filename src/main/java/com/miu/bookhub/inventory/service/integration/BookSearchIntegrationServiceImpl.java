package com.miu.bookhub.inventory.service.integration;

import com.miu.bookhub.inventory.repository.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookSearchIntegrationServiceImpl implements BookSearchIntegrationService{

    @Override
    public Optional<Book> searchBookByIsbn(String isbn) {
        return Optional.empty();
    }
}
