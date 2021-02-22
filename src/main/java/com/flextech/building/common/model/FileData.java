package com.flextech.building.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileData {
    private byte[] data;
    private BufferedImage image;
    private String path;
    private MediaType mediaType;
}
