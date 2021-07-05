package com.flextech.building.entity.pojo;

import lombok.Data;

@Data
public class BlueprintAnalysisBuildingSite {
    private Double siteArea;

    private Double floorAreaRatioLimit;

    private Double floorAreaRatioLegalMultiplier;

    private Double floorAreaRatioLimitAddingValue;

    private Double minFloorAreaRatioLimit;
}
