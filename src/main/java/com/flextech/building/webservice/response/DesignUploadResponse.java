package com.flextech.building.webservice.response;

import com.flextech.building.webservice.request.FileMeta;
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
    private String fileName;
    private String path;
    private String mediaType;

    @Builder.Default
    private List<String> imageList = new ArrayList<>();

    @Builder.Default
    private List<FileMeta> fileMetaInfos = new ArrayList<>();
}
