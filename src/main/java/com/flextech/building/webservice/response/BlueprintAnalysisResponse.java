package com.flextech.building.webservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlueprintAnalysisResponse {

    private String message;

    private List<String> imgs = new ArrayList<>();

    private List<ImageResponse> externalShapeDrawnImgs = new ArrayList<>();

    private List<ImageResponse> fixtureSymbolDrawnImgs = new ArrayList<>();

    private BuildingSiteResponse buildingSite;

    private BuildingResponse building;

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    @JsonProperty("externalShapeDrawnImgs")
    public List<ImageResponse> getExternalShapeDrawnImgs() {
        return externalShapeDrawnImgs;
    }

    @JsonProperty("external_shape_drawn_imgs")
    public void setExternalShapeDrawnImgs(List<ImageResponse> externalShapeDrawnImgs) {
        this.externalShapeDrawnImgs = externalShapeDrawnImgs;
    }

    @JsonProperty("fixtureSymbolDrawnImgs")
    public List<ImageResponse> getFixtureSymbolDrawnImgs() {
        return fixtureSymbolDrawnImgs;
    }

    @JsonProperty("fixture_symbol_drawn_imgs")
    public void setFixtureSymbolDrawnImgs(List<ImageResponse> fixtureSymbolDrawnImgs) {
        this.fixtureSymbolDrawnImgs = fixtureSymbolDrawnImgs;
    }

    @JsonProperty("buildingSite")
    public BuildingSiteResponse getBuildingSite() {
        return buildingSite;
    }

    @JsonProperty("building_site")
    public void setBuildingSite(BuildingSiteResponse buildingSite) {
        this.buildingSite = buildingSite;
    }

    public BuildingResponse getBuilding() {
        return building;
    }

    public void setBuilding(BuildingResponse building) {
        this.building = building;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
