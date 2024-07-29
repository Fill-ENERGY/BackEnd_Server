package com.example.template.domain.block.repository;

import com.example.template.domain.block.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
