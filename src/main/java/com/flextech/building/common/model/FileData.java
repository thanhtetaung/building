package com.flextech.building.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class FileData implements Comparable<FileData> {
    @JsonIgnore
    private int index;
    private byte[] data;
    private BufferedImage image;
    private String path;
    private String url;
    private MediaType mediaType;

    @Override
    public int compareTo(FileData o) {
        if (getIndex() > o.getIndex()) {
            return 1;
        } else if (getIndex() < o.getIndex()) {
            return -1;
        } else {
            return 0;
        }
    }
}
