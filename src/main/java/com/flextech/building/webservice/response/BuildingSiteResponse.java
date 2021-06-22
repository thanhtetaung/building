package com.flextech.building.webservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildingSiteResponse {

    private Double siteArea;

    private Double floorAreaRatioLimit;

    private Double floorAreaRatioLegalMultiplier;

    private Double floorAreaRatioLimitAddingValue;

    private Double minFloorAreaRatioLimit;

    @JsonProperty("siteArea")
    public Double getSiteArea() {
        return siteArea;
    }

    @JsonProperty("site_area")
    public void setSiteArea(Double siteArea) {
        this.siteArea = siteArea;
    }

    @JsonProperty("floorAreaRatioLimit")
    public Double getFloorAreaRatioLimit() {
        return floorAreaRatioLimit;
    }

    @JsonProperty("floor_area_ratio_limit")
    public void setFloorAreaRatioLimit(Double floorAreaRatioLimit) {
        this.floorAreaRatioLimit = floorAreaRatioLimit;
    }

    @JsonProperty("floorAreaRatioLegalMultiplier")
    public Double getFloorAreaRatioLegalMultiplier() {
        return floorAreaRatioLegalMultiplier;
    }

    @JsonProperty("floor_area_ratio_legal_multiplier")
    public void setFloorAreaRatioLegalMultiplier(Double floorAreaRatioLegalMultiplier) {
        this.floorAreaRatioLegalMultiplier = floorAreaRatioLegalMultiplier;
    }

    @JsonProperty("floorAreaRatioLimitAddingValue")
    public Double getFloorAreaRatioLimitAddingValue() {
        return floorAreaRatioLimitAddingValue;
    }

    @JsonProperty("floor_area_ratio_limit_adding_value")
    public void setFloorAreaRatioLimitAddingValue(Double floorAreaRatioLimitAddingValue) {
        this.floorAreaRatioLimitAddingValue = floorAreaRatioLimitAddingValue;
    }

    @JsonProperty("minFloorAreaRatioLimit")
    public Double getMinFloorAreaRatioLimit() {
        return minFloorAreaRatioLimit;
    }

    @JsonProperty("min_floor_area_ratio_limit")
    public void setMinFloorAreaRatioLimit(Double minFloorAreaRatioLimit) {
        this.minFloorAreaRatioLimit = minFloorAreaRatioLimit;
    }
}
