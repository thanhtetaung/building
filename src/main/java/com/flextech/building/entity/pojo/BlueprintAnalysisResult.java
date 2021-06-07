package com.flextech.building.entity.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BlueprintAnalysisResult {

    private String message;

    private List<String> imgs = new ArrayList<>();

    private List<Image> externalShapeDrawnImgs = new ArrayList<>();

    private List<Image> fixtureSymbolDrawnImgs = new ArrayList<>();

    private BuildingSite buildingSite;

    private BuildingInfo building;
}
