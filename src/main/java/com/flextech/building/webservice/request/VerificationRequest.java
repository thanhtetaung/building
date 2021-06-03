package com.flextech.building.webservice.request;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class VerificationRequest {

    @NotNull(message = "{error.validation.email.empty}")
    @Email(message = "{error.validation.email.invalid}")
    private String email;

    @NotNull(message = "{error.validation.otp.empty}")
    private String otp;

}
