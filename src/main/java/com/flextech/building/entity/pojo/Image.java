package com.flextech.building.entity.pojo;

import com.flextech.building.webservice.response.Symbol;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Image {
    private String img;
    private Integer floor;
    private Integer area;
    private Double detectedArea;
    private String fixtureSymbolImg;
    private String blueprintType;
    private List<Symbol> fixtureSymbols = new ArrayList<>();
}
