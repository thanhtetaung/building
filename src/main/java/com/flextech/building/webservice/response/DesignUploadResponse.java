package com.flextech.building.webservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DesignUploadResponse {
    private String path;
    private String mediaType;
    private List<String> imageList = new ArrayList<>();
}
