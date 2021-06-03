package com.flextech.building.webservice.request;

import com.flextech.building.common.validation.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotNull(message = "{error.validation.username.empty}")
    @Size(min = 5, max = 50, message = "{error.validation.username.length}")
    private String username;

    @NotNull(message = "{error.validation.password.empty}")
    @ValidPassword(message = "{error.validation.password.invalid}")
    private String password;

    @NotNull(message = "{error.validation.firstName.empty}")
    @Size(max = 50, message = "{error.validation.firstName.length}")
    private String firstName;

    @NotNull(message = "{error.validation.lastName.empty}")
    @Size(max = 50, message = "{error.validation.lastName.length}")
    private String lastName;

    @NotNull(message = "{error.validation.email.empty}")
    @Email(message = "{error.validation.email.invalid}")
    private String email;

    @NotNull(message = "{error.validation.otp.empty}")
    private String otp;

}
