package com.flextech.building.webservice.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private Double startDate;
    private Double stopDate;
    private ProcessingStatus status;
    private String traceHeader;
    private String stateMachineArn;
    private String name;
    private String executionArn;
    private String output;

    public BlueprintAnalysisResponse getBlueprintAnalysisResponse() {
        try {
            output =output.replace("\\\"", "\"");

            if (output.startsWith("\"")) {
                output = output.substring(1);
            }
            if (output.endsWith("\"")) {
                output = output.substring(0, output.length() - 1);
            }
            BlueprintAnalysisResponse response = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(output, BlueprintAnalysisResponse.class);
            return response;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
