package com.miu.bookhub.inventory.repository;

import com.miu.bookhub.inventory.repository.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAllByTitleLikeIgnoreCase(String title, Pageable pageable);

    List<Book> findAllByAuthorsNameLikeIgnoreCase(String name, Pageable pageable);
}
