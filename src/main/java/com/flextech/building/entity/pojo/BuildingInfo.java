package com.flextech.building.entity.pojo;

import com.flextech.building.webservice.response.Floor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BuildingInfo {
    private Double buildingArea;

    private Double totalFloorArea;

    private Double buildingCoverageRatio;

    private Double floorAreaRatio;

    private Double heightOfBuilding;

    private Double numberOfFloors;

    private List<Floor> floors = new ArrayList<>();
}
