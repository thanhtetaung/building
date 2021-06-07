package com.flextech.building.entity;

import com.flextech.building.entity.enums.ProcessingStatus;
import com.flextech.building.entity.pojo.BlueprintAnalysisResult;
import com.flextech.building.webservice.request.FileMeta;
import com.flextech.building.webservice.response.DesignUploadResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document
public class Building extends BaseEntity {

    @Id
    private String id;

    private String name;

    private String userId;

    private Double siteArea;

    private String fireZone;

    private Double totalFloorArea;

    private Double floorAreaRatio;

    private Double buildingCoverageRatio;

    private Double buildingArea;

    private Double frontalRoadWidth;

    private Integer numberOfAboveGroundFloors;

    private Integer numberOfBasementFloors;

    private Boolean specialRoadExistence;

    private Double heightOfBuilding;

    private Double distanceToSpecialRoad;

    private String useDistricts;

    private Boolean buildingCoverageRatioDeregulation = false;

    private List<String> files = new ArrayList<>();

    private List<List<FileMeta>> fileMetaInfos = new ArrayList<>();

    private List<DesignUploadResponse> imageMetaInfos = new ArrayList<>();

    private List<Double> floorAreas = new ArrayList<>();

    private BlueprintAnalysisResult result;

    private String executionArn;

    private Double startDate;

    private ProcessingStatus processingStatus;


}
