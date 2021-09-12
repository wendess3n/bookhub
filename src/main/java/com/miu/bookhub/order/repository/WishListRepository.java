package com.miu.bookhub.order.repository;

import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.inventory.repository.entity.Book;
import com.miu.bookhub.order.repository.entity.WishList;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WishListRepository extends CrudRepository<WishList, Long> {

    Optional<WishList> findByCustomerAndBook(User user, Book book);
}
