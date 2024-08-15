package com.example.template.domain.complaint.repository;

import com.example.template.domain.complaint.entity.ComplaintImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ComplaintImgRepository extends JpaRepository<ComplaintImg, Long> {

    @Query("SELECT c FROM ComplaintImg c WHERE c.complaint.id = :complaintId")
    List<ComplaintImg> findAllByComplaintId(@Param("complaintId")Long complaintId);


    List<ComplaintImg> findAllByImgUrlIn(List<String> images);

    @Query("SELECT c FROM ComplaintImg  c WHERE c.complaint IS NULL")
    List<ComplaintImg> findUnmappedImages();
}
