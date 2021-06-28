package com.flextech.building.webservice.request;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class FileMeta {

    private Boolean include;

    @NotNull(message = "{error.validation.blueprintType.empty}")
    private String blueprintType;

    private List<String> direction;

    @Digits(integer = 10, fraction = 8,message = "{error.validation.area.notDigit}")
    private Double area;

    private List<Integer> floors;
}
