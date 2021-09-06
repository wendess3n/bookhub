package com.miu.bookhub.account.repository;

import com.miu.bookhub.account.repository.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
