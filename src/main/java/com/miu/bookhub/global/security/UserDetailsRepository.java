package com.miu.bookhub.global.security;

import com.miu.bookhub.account.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDetailsRepository implements UserDetailsService {

    private final RegistrationService registrationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return registrationService.findUserByEmail(username).orElse(null);
    }
}
