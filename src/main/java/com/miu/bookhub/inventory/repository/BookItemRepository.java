package com.miu.bookhub.inventory.repository;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.inventory.repository.entity.BookItem;
import com.miu.bookhub.inventory.repository.entity.Condition;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookItemRepository extends CrudRepository<BookItem, Long> {

    Optional<BookItem> findByBookAndConditionAndSeller(Book book, Condition condition, User seller);
}
