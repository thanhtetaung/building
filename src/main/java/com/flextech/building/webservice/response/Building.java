package com.flextech.building.webservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Building {

    private Double buildingArea;

    private Double totalFloorArea;

    private Double buildingCoverageRatio;

    private Double floorAreaRatio;

    private Double heightOfBuilding;

    private Double numberOfFloors;

    private List<Floor> floors = new ArrayList<>();

    @JsonProperty("buildingArea")
    public Double getBuildingArea() {
        return buildingArea;
    }

    @JsonProperty("building_area")
    public void setBuildingArea(Double buildingArea) {
        this.buildingArea = buildingArea;
    }

    @JsonProperty("totalFloorArea")
    public Double getTotalFloorArea() {
        return totalFloorArea;
    }

    @JsonProperty("total_floor_area")
    public void setTotalFloorArea(Double totalFloorArea) {
        this.totalFloorArea = totalFloorArea;
    }

    @JsonProperty("buildingCoverageRatio")
    public Double getBuildingCoverageRatio() {
        return buildingCoverageRatio;
    }

    @JsonProperty("building_coverage_ratio")
    public void setBuildingCoverageRatio(Double buildingCoverageRatio) {
        this.buildingCoverageRatio = buildingCoverageRatio;
    }

    @JsonProperty("floorAreaRatio")
    public Double getFloorAreaRatio() {
        return floorAreaRatio;
    }

    @JsonProperty("floor_area_ratio")
    public void setFloorAreaRatio(Double floorAreaRatio) {
        this.floorAreaRatio = floorAreaRatio;
    }

    @JsonProperty("heightOfBuilding")
    public Double getHeightOfBuilding() {
        return heightOfBuilding;
    }

    @JsonProperty("height_of_building")
    public void setHeightOfBuilding(Double heightOfBuilding) {
        this.heightOfBuilding = heightOfBuilding;
    }

    @JsonProperty("numberOfFloors")
    public Double getNumberOfFloors() {
        return numberOfFloors;
    }

    @JsonProperty("number_of_floors")
    public void setNumberOfFloors(Double numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
}
