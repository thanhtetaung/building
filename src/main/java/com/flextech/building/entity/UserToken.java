package com.flextech.building.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserToken extends BaseEntity {
    @Id
    private String id;

    private String token;

    @DBRef
    private User user;
}
