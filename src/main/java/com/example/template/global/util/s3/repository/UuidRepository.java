package com.example.template.global.util.s3.repository;

import com.example.template.global.util.s3.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
