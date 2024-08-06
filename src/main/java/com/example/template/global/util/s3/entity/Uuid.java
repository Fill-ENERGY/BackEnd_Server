package com.example.template.global.util.s3.entity;

import com.example.template.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uuid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uuid_id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String uuid;
}
