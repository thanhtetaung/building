package com.flextech.building.entity.pojo;

import lombok.Data;

@Data
public class BuildingSite {
    private Double siteArea;

    private Double floorAreaRatioLimit;

    private Double floorAreaRatioLegalMultiplier;

    private Double floorAreaRatioLimitAddingValue;

    private Double minFloorAreaRatioLimit;
}
