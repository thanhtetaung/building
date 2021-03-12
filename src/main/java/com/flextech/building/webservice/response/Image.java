package com.flextech.building.webservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Image {
    private String img;
    private Integer floor;
    private Integer area;
    private Double detectedArea;
    private String fixtureSymbolImg;
    private String blueprintType;
    private List<Symbol> fixtureSymbols = new ArrayList<>();

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    @JsonProperty("detectedArea")
    public Double getDetectedArea() {
        return detectedArea;
    }

    @JsonProperty("detected_area")
    public void setDetectedArea(Double detectedArea) {
        this.detectedArea = detectedArea;
    }


    @JsonProperty("fixtureSymbolImg")
    public String getFixtureSymbolImg() {
        return fixtureSymbolImg;
    }

    @JsonProperty("fixture_symbol_img")
    public void setFixtureSymbolImg(String fixtureSymbolImg) {
        this.fixtureSymbolImg = fixtureSymbolImg;
    }

    @JsonProperty("blueprintType")
    public String getBlueprintType() {
        return blueprintType;
    }

    @JsonProperty("blueprint_type")
    public void setBlueprintType(String blueprintType) {
        this.blueprintType = blueprintType;
    }

    @JsonProperty("fixtureSymbols")
    public List<Symbol> getFixtureSymbols() {
        return fixtureSymbols;
    }

    @JsonProperty("fixture_symbols")
    public void setFixtureSymbols(List<Symbol> fixtureSymbols) {
        this.fixtureSymbols = fixtureSymbols;
    }
}
