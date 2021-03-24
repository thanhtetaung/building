package com.flextech.building.webservice.request;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class BlueprintAnalysisRequest {

    @NotNull(message = "{error.validation.siteArea.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double siteArea;

    @NotNull(message = "{error.validation.fireZone.empty}")
    private String fireZone;

    @NotNull(message = "{error.validation.totalFloorArea.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double totalFloorArea;

    @NotNull(message = "{error.validation.floorAreaRatio.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double floorAreaRatio;

    @NotNull(message = "{error.validation.buildingCoverageRatio.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double buildingCoverageRatio;

    @NotNull(message = "{error.validation.buildingArea.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double buildingArea;

    @NotNull(message = "{error.validation.frontalRoadWidth.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double frontalRoadWidth;

    @NotNull(message = "{error.validation.numberOfAboveGroundFloors.empty}")
    @Digits(integer = 5, fraction = 0)
    private Integer numberOfAboveGroundFloors;

    @NotNull(message = "{error.validation.numberOfBasementFloors.empty}")
    @Digits(integer = 5, fraction = 0)
    private Integer numberOfBasementFloors;

    @Pattern(regexp = "^true$|^false$", message = "{error.validation.specialRoadExistence.invalid}")
    private Boolean specialRoadExistence;

    @NotNull(message = "{error.validation.heightOfBuilding.empty}")
    @Digits(integer = 10, fraction = 8)
    private Double heightOfBuilding;

    @Digits(integer = 10, fraction = 8)
    private Double distanceToSpecialRoad;

    @NotNull(message = "{error.validation.useDistricts.empty}")
    private String useDistricts;

    private Boolean buildingCoverageRatioDeregulation = false;

    @NotEmpty(message = "{error.validation.files.empty}")
    private List<String> files = new ArrayList<>();

    @NotEmpty(message = "{error.validation.fileMetaInfos.empty}")
    private List<List<FileMeta>> fileMetaInfos = new ArrayList<>();

    private List<Double> floorAreas = new ArrayList<>();

    public Map<String, Object> json() {
        Map<String, Object> map = new HashMap<>();
        map.put("site_area", siteArea);
        map.put("fire_zone", fireZone);
        map.put("total_floor_area", totalFloorArea);
        map.put("floor_area_ratio", floorAreaRatio);
        map.put("building_coverage_ratio", buildingCoverageRatio);
        map.put("building_area", buildingArea);
        map.put("number_of_above_ground_floors", numberOfAboveGroundFloors);
        map.put("number_of_basement_floors", numberOfBasementFloors);
        map.put("floor_areas", floorAreas);
        if (frontalRoadWidth != null) {
            map.put("frontal_road_width", frontalRoadWidth);
        }

        if (specialRoadExistence != null) {
            map.put("special_road_existence", specialRoadExistence);
        }

        map.put("height_of_building", heightOfBuilding);
        if (distanceToSpecialRoad != null) {
            map.put("distance_to_special_road", distanceToSpecialRoad);
        }
        map.put("use_districts", Collections.singleton(useDistricts));
        map.put("building_coverage_ratio_deregulation", buildingCoverageRatioDeregulation);
        map.put("files", files);
        List<List<Map<String, Object>>> metas = fileMetaInfos.stream().map(fileMetas -> fileMetas.stream().map(fileMeta -> {
            Map<String, Object> meta = new HashMap<>();
            meta.put("blueprint_type", fileMeta.getBlueprintType());
            if (fileMeta.getDirection() != null) {
                meta.put("direction", fileMeta.getDirection());
            }
            if (fileMeta.getArea() != null) {
                meta.put("area", fileMeta.getArea());
            }
            if (fileMeta.getFloors() != null) {
                meta.put("floors", Collections.singleton(fileMeta.getFloors()));
            }
            return meta;
        }).collect(Collectors.toList()))
                .collect(Collectors.toList());
        map.put("file_meta_infos", metas);
        return map;
    }

}
