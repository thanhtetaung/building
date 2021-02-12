package com.flextech.building.common.webservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {
    private String fieldName;
    private Object rejectedValue;
    private String errorMessage;
}
