package com.flextech.building.entity;

import com.flextech.building.entity.enums.Indicator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document
public class Otp extends BaseEntity {
    @Id
    private String id;

    @Indexed
    private String linkId;

    private String otp;

    private Indicator usedInd;

    private LocalDateTime expireDateTime;
}
