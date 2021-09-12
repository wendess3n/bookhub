package com.miu.bookhub.account.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddressResponse {

    private Long addressId;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String addressLine1;
    private String addressLine2;
}
