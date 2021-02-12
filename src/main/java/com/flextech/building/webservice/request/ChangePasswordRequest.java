package com.flextech.building.webservice.request;

import com.flextech.building.common.validation.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequest {

    @NotNull(message = "{error.validation.oldPassword.empty}")
    String oldPassword;

    @NotNull(message = "{error.validation.newPassword.empty}")
    @ValidPassword(message = "{error.validation.newPassword.invalid}")
    String newPassword;
}
