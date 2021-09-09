package com.miu.bookhub.account.api.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRequest {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
}
