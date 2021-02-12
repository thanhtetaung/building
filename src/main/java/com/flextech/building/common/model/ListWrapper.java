package com.flextech.building.common.model;

import lombok.Data;

import java.util.List;

@Data
public class ListWrapper<T> {
    long page;
    long totalPages;
    long size;
    long totalRecords;

    List<T> content;
}
