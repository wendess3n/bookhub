package com.miu.bookhub.account.api;

import com.miu.bookhub.account.api.domain.UserRequest;
import com.miu.bookhub.account.api.domain.UserResponse;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class RegistrationController {

    private final ModelMapper modelMapper;
    private final RegistrationService registrationService;

    @PostMapping
    public UserResponse saveUser(@RequestBody UserRequest userRequest) {

        User customer = registrationService.registerCustomer(userRequest.getFirstName(),
                userRequest.getLastName(), userRequest.getEmailAddress(), userRequest.getPassword());

        return buildUserResponse(customer);
    }

    @GetMapping("/{userId}")
    public UserResponse findUserByUserId(@PathVariable Long userId) {

        return registrationService.findUserById(userId)
                .map(this::buildUserResponse)
                .orElse(null);
    }

    @GetMapping(params = "email")
    public UserResponse findUserByEmailAddress(@RequestParam String email) {

        return registrationService.findUserByEmail(email)
                .map(this::buildUserResponse)
                .orElse(null);
    }

    @PutMapping("/{userId}/roles/seller")
    public UserResponse upgradeAccountToSeller(@PathVariable long userId) {

        User seller = registrationService.upgradeAccountToSeller(userId);
        return buildUserResponse(seller);
    }

    @PostAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/lock")
    public void lockAccount(@PathVariable long userId) {
        registrationService.lockAccount(userId, false);
    }

    @DeleteMapping("/{userId}")
    public void disableAccount(@PathVariable long userId) {
        registrationService.disableAccount(userId);
    }

    private UserResponse buildUserResponse(User user) {

       var userResponse = modelMapper.map(user, UserResponse.class);
       userResponse.setUserId(user.getId());
       return userResponse;
    }
}
