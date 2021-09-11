package com.miu.bookhub.account.service;

import com.miu.bookhub.account.exception.UserServiceException;
import com.miu.bookhub.account.repository.UserRepository;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.global.utils.SecurityUtils;
import com.miu.bookhub.global.i18n.DefaultMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final static MessageSourceAccessor messages = DefaultMessageSource.getAccessor();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerCustomer(String firstName, String lastName, String emailAddress, String password) {

        validateNewCustomer(firstName, lastName, emailAddress, password);

        try {

            User customer = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .emailAddress(emailAddress)
                    .credential(passwordEncoder.encode(password))
                    .isLocked(false)
                    .isActive(true)
                    .roles(Set.of(Role.CUSTOMER))
                    .build();

            return userRepository.save(customer);

        } catch (Exception ex) {

            log.error("Failed to register customer", ex);
            throw new UserServiceException(ex.getMessage());
        }
    }

    @Override
    public Optional<User> findUserById(long userId) {

        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String emailAddress) {

        return userRepository.findByEmailAddress(emailAddress);
    }

    @Override
    public User upgradeAccountToSeller(long userId) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) throw new UserServiceException("user.id.invalid");

        User customer = user.get();
        User actor = SecurityUtils.getCurrentUser();

        // Access is granted for the same user or admin only
        if (actor == null || (!actor.getId().equals(userId) && !actor.getRoles().contains(Role.ADMIN))) {
            throw new UserServiceException(messages.getMessage("access.denied"));
        }

        if (!customer.getRoles().contains(Role.SELLER)) {

            Set<Role> newRoles = new HashSet<>(customer.getRoles());
            newRoles.add(Role.SELLER);
            customer.setRoles(newRoles);

            customer = userRepository.save(customer);
        }

        return customer;
    }

    @Override
    public void disableAccount(long userId) {

        userRepository.findById(userId)
                        .ifPresentOrElse(user -> {
                            User actor = SecurityUtils.getCurrentUser();

                            // Access is granted for the same user or admin only
                            if (actor == null || (!actor.getId().equals(userId) && !actor.getRoles().contains(Role.ADMIN))) {
                                throw new UserServiceException(messages.getMessage("access.denied"));
                            }

                            user.setIsActive(false);
                            userRepository.save(user);

                        }, () -> {
                            throw new UserServiceException("user.id.invalid");
                        });
    }

    @Override
    public void lockAccount(long userId, boolean bySystem) {

        userRepository.findById(userId)
                .ifPresentOrElse(user -> {

                    User actor = SecurityUtils.getCurrentUser();

                    // Access is granted for admin or for the system only
                    if (!bySystem && (actor == null || !actor.getRoles().contains(Role.ADMIN))) {
                        throw new UserServiceException(messages.getMessage("access.denied"));
                    }

                    user.setIsLocked(true);
                    userRepository.save(user);

                }, () -> {
                    throw new UserServiceException("user.id.invalid");
                });
    }

    private void validateNewCustomer(String firstName, String lastName, String emailAddress, String password) {

        if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
            throw new UserServiceException(messages.getMessage("user.name.blank"));
        }

        if (StringUtils.isBlank(emailAddress) || !VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress).matches()) {
            throw new UserServiceException(messages.getMessage("user.email.invalid"));
        }

        if (userRepository.findByEmailAddress(emailAddress).isPresent()) {
            throw new UserServiceException(messages.getMessage("user.email.taken"));
        }

        if (StringUtils.isBlank(password) || password.length() < 5) {
            throw new UserServiceException(messages.getMessage("user.password.weak"));
        }
    }
}
