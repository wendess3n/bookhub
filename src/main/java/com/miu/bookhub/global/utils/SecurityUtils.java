package com.miu.bookhub.global.utils;

import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.global.i18n.DefaultMessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private final static MessageSourceAccessor messages = DefaultMessageSource.getAccessor();

    private SecurityUtils() {}

    public static User getCurrentUser() {

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (User) auth.getPrincipal())
                .orElse(null);
    }

    public static Long getCurrentUserId() {

        return Optional.ofNullable(getCurrentUser())
                .map(User::getId)
                .orElse(null);
    }

    public static void validateAuthorizationOnResource(long userId) {

        User actor = SecurityUtils.getCurrentUser();

        if (actor == null || (!actor.getId().equals(userId) && !actor.getRoles().contains(Role.ADMIN)))
            throw new AccessDeniedException(messages.getMessage("access.denied"));
    }
}
