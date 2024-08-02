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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uuid_seq_generator")
    @SequenceGenerator(name = "uuid_seq_generator", sequenceName = "UUID_SEQ", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String uuid;
}
