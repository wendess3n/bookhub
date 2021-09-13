package com.miu.bookhub.inventory.repository;

import com.miu.bookhub.inventory.repository.entity.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
