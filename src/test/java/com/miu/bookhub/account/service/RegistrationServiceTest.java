package com.miu.bookhub.account.service;

import com.miu.bookhub.TestConfig;
import com.miu.bookhub.account.exception.UserServiceException;
import com.miu.bookhub.account.repository.AddressRepository;
import com.miu.bookhub.account.repository.UserRepository;
import com.miu.bookhub.account.repository.entity.Role;
import com.miu.bookhub.account.repository.entity.User;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@Import(TestConfig.class)
@ExtendWith(SpringExtension.class)
public class RegistrationServiceTest {

    @MockBean private UserRepository userRepository;
    @MockBean private AddressRepository addressRepository;
    private RegistrationService registrationService;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @BeforeEach
    void setup() {
        registrationService = new RegistrationServiceImpl(userRepository, passwordEncoder, addressRepository);
    }

    @Test
    void shouldSuccessfullyRegisterCustomer() {

        String firstName = "Abel";
        String lastName = "Adam";
        String email = "abel.adam@email.com";
        String password = "Password1";

        when(userRepository.findByEmailAddress(anyString()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .then(inv -> {
                    User user = inv.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        User customer = registrationService.registerCustomer(firstName, lastName, email, password);

        assertThat(customer.getId())
                .as("Expected customer to have id")
                .isNotNull();

        assertThat(customer.getFirstName())
                .as("Excepted first name to be %s", firstName)
                .isEqualTo(firstName);

        assertThat(customer.getLastName())
                .as("Expected last name to be %s", lastName)
                .isEqualTo(lastName);

        assertThat(customer.getEmailAddress())
                .as("Expected email address to be %s", email)
                .isEqualTo(email);

        Condition<String> passwordMatcher = new Condition<>() {
            @Override
            public boolean matches(String value) {
                return passwordEncoder.matches(password, value);
            }
        };

        assertThat(customer.getCredential())
                .as("Expected password to match to %s", password)
                .is(passwordMatcher);
    }

    @Test
    void shouldFailToRegisterCustomer_WeakPassword() {

        String firstName = "Abel";
        String lastName = "Adam";
        String email = "abel.adam@email.com";
        String password = "123";

        when(userRepository.findByEmailAddress(anyString()))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .then(inv -> {
                    User user = inv.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        assertThatExceptionOfType(UserServiceException.class)
                .as("Expected registration to fail b/c of weak password")
                .isThrownBy(() -> registrationService.registerCustomer(firstName, lastName, email, password));
    }

    @Test
    void shouldFindUserByEmailAddress() {

        String email = TestConfig.TEST_USER_NAME;

        when(userRepository.findByEmailAddress(eq(email)))
                .thenReturn(Optional.of(buildMockUser()));

        Optional<User> user = registrationService.findUserByEmail(email);

        assertThat(user)
                .as("Expected to find user with email address %s", email)
                .isNotEmpty();
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldUpgradeCustomerToSeller() {

        User customer = buildMockUser();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(customer));

        when(userRepository.save(any(User.class)))
                .then(inv -> inv.getArgument(0, User.class));

        User seller = registrationService.upgradeAccountToSeller(TestConfig.TEST_USER_ID);

        assertThat(new ArrayList<>(seller.getRoles()))
                .as("Expected user to have seller role")
                .asList()
                .contains(Role.SELLER);

    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldDisableAccount() {

        User user = buildMockUser();

        when(userRepository.findById(eq(TestConfig.TEST_USER_ID)))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .then(inv -> inv.getArgument(0, User.class));

        registrationService.disableAccount(TestConfig.TEST_USER_ID);

        var argumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(1)).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getIsActive())
                .as("Expected user to be disabled")
                .isFalse();
    }

    @WithUserDetails(userDetailsServiceBeanName = "mockUserDetailsService")
    @Test
    void shouldLockAccount() {

        User user = buildMockUser();

        when(userRepository.findById(eq(TestConfig.TEST_USER_ID)))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .then(inv -> inv.getArgument(0, User.class));

        registrationService.lockAccount(TestConfig.TEST_USER_ID, true);

        var argumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository, times(1)).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getIsLocked())
                .as("Expected user to be locked")
                .isTrue();
    }

    private User buildMockUser() {

        return User.builder()
                .id(TestConfig.TEST_USER_ID)
                .firstName("Abel")
                .lastName("Adam")
                .emailAddress(TestConfig.TEST_USER_NAME)
                .isActive(true)
                .isLocked(false)
                .roles(Set.of(Role.CUSTOMER))
                .build();
    }
}
