package com.example.template.domain.message.repository;

import com.example.template.domain.message.entity.MessageImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageImgRepository extends JpaRepository<MessageImg, Long> {
    List<MessageImg> findAllByImgUrlIn(List<String> images);

    @Query("SELECT m FROM MessageImg m WHERE m.message IS NULL")
    List<MessageImg> findUnmappedImages();
}
