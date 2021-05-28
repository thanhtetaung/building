package com.flextech.building.webservice.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateProfileRequest {
    @NotNull(message = "{error.validation.firstName.empty}")
    @Size(max = 50, message = "{error.validation.firstName.length}")
    private String firstName;

    @NotNull(message = "{error.validation.lastName.empty}")
    @Size(max = 50, message = "{error.validation.lastName.length}")
    private String lastName;
}
