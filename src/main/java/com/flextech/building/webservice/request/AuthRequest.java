package com.flextech.building.webservice.request;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class AuthRequest {

    @NotNull(message = "{error.validation.username.empty}")
    private String username;

    @NotNull(message = "{error.validation.password.empty}")
    private String password;
}
