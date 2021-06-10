package com.flextech.building.webservice.response;

import com.flextech.building.entity.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class BluePrintAnalysisResponseWrapper {
    private ProcessingStatus status;
    private BlueprintAnalysisResponse output;
}
