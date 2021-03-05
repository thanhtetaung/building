package com.flextech.building.webservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor

public class BlueprintAnalysisResponse {
    private List<Image> imgs = new ArrayList<>();

    private List<Image> externalShapeDrawnImgs = new ArrayList<>();

    private List<Image> fixtureSymbolDrawnImgs = new ArrayList<>();

    private BuildingSite buildingSite;

    private Building building;

    public List<Image> getImgs() {
        return imgs;
    }

    public void setImgs(List<Image> imgs) {
        this.imgs = imgs;
    }

    @JsonProperty("externalShapeDrawnImgs")
    public List<Image> getExternalShapeDrawnImgs() {
        return externalShapeDrawnImgs;
    }

    @JsonProperty("external_shape_drawn_imgs")
    public void setExternalShapeDrawnImgs(List<Image> externalShapeDrawnImgs) {
        this.externalShapeDrawnImgs = externalShapeDrawnImgs;
    }

    @JsonProperty("fixtureSymbolDrawnImgs")
    public List<Image> getFixtureSymbolDrawnImgs() {
        return fixtureSymbolDrawnImgs;
    }

    @JsonProperty("fixture_symbol_drawn_imgs")
    public void setFixtureSymbolDrawnImgs(List<Image> fixtureSymbolDrawnImgs) {
        this.fixtureSymbolDrawnImgs = fixtureSymbolDrawnImgs;
    }

    @JsonProperty("buildingSite")
    public BuildingSite getBuildingSite() {
        return buildingSite;
    }

    @JsonProperty("building_site")
    public void setBuildingSite(BuildingSite buildingSite) {
        this.buildingSite = buildingSite;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}
