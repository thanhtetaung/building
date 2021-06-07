package com.flextech.building.webservice.request;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Data
public class FileMeta {

    private Boolean include;

    @NotNull(message = "{error.validation.blueprintType.empty}")
    private String blueprintType;

    private String direction;

    @Digits(integer = 10, fraction = 8,message = "{error.validation.area.notDigit}")
    private Double area;

    @Digits(integer = 5, fraction = 0,message = "{error.validation.floors.notDigit}")
    private Integer floors;
}
