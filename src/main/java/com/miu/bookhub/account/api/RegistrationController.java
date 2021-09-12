package com.miu.bookhub.account.api;

import com.miu.bookhub.account.api.domain.AddressRequest;
import com.miu.bookhub.account.api.domain.AddressResponse;
import com.miu.bookhub.account.api.domain.UserRequest;
import com.miu.bookhub.account.api.domain.UserResponse;
import com.miu.bookhub.account.repository.entity.Address;
import com.miu.bookhub.account.repository.entity.User;
import com.miu.bookhub.account.service.RegistrationService;
import com.miu.bookhub.global.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/{userId}/addresses")
    public AddressResponse saveAddress(@PathVariable long userId, @RequestBody AddressRequest request) {

        Address address = registrationService.saveCustomerAddress(userId, request.getCountry(),
                request.getState(), request.getCity(), request.getZipCode(), request.getAddressLine1(), request.getAddressLine2());

        return buildAddressResponse(address);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    public AddressResponse getAddressById(@PathVariable long userId, @PathVariable long addressId) {

        return registrationService.findAddressById(userId, addressId)
                .map(this::buildAddressResponse)
                .orElse(null);
    }

    @GetMapping("/{userId}/addresses")
    public List<AddressResponse> getAddresses(@PathVariable long userId) {

        return registrationService.findAddresses(SecurityUtils.getCurrentUserId()).stream()
                .map(this::buildAddressResponse)
                .collect(Collectors.toList());
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

    private AddressResponse buildAddressResponse(Address address) {

        var addressResponse = modelMapper.map(address, AddressResponse.class);
        addressResponse.setAddressId(address.getId());
        return addressResponse;
    }
}
