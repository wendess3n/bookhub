package com.miu.bookhub.account.service;

import com.miu.bookhub.account.repository.entity.User;

import java.util.Optional;

public interface RegistrationService {

    User registerCustomer(String firstName, String lastName, String emailAddress, String password);

    Optional<User> findUserById(long userId);

    Optional<User> findUserByEmail(String emailAddress);

    User upgradeAccountToSeller(long userId);

    void disableAccount(long userId);

    void lockAccount(long userId, boolean bySystem);
}
