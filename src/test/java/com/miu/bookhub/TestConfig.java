package com.miu.bookhub;

import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

@TestConfiguration
public class TestConfig {

    public static final long TEST_USER_ID = 23;
    public static final String TEST_USER_NAME = "abel.adam@email.com";

    @Bean("mockUserDetailsService")
    public UserDetailsService userDetailsService() {
        return username -> User.builder()
                .id(TEST_USER_ID)
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress(TEST_USER_NAME)
                .roles(Set.of(Role.CUSTOMER, Role.SELLER))
                .isLocked(false)
                .isActive(true)
                .build();
    }
}
