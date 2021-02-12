package com.flextech.building.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
abstract public class BaseEntity {

    @LastModifiedDate
    private LocalDateTime lastUpdateDate;

    @LastModifiedBy
    private String lastUpdateBy;

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private String createdBy;

    @Version
    private Long version;
}
