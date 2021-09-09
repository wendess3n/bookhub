package com.miu.bookhub.global.utils;

import com.miu.bookhub.account.repository.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (User) auth.getPrincipal())
                .orElse(null);
    }
}
