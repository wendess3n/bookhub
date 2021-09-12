package com.miu.bookhub.account.service;

import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.User;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {

    User registerCustomer(String firstName, String lastName, String emailAddress, String password);

    Optional<User> findUserById(long userId);

    Optional<User> findUserByEmail(String emailAddress);

    Address saveCustomerAddress(long customerId, String country, String state,
                                String city, String zipCode, String addressLine1, String addressLine2);

    Optional<Address> findAddressById(long customerId, long addressId);

    List<Address> findAddresses(long customerId);

    User upgradeAccountToSeller(long userId);

    void disableAccount(long userId);

    void lockAccount(long userId, boolean bySystem);
}
