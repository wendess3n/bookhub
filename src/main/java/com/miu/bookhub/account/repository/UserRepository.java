package com.miu.bookhub.account.repository;

import com.miu.bookhub.account.repository.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmailAddress(String emailAddress);
}
