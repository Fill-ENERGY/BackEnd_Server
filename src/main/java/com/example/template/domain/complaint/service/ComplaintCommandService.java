package com.example.template.domain.complaint.service;

import com.example.template.domain.complaint.dto.request.ComplaintRequestDTO;
import com.example.template.domain.complaint.dto.response.ComplaintResponseDTO;
import com.example.template.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComplaintCommandService {
    ComplaintResponseDTO.ComplaintImgDTO uploadImages(List<MultipartFile> images);

    ComplaintResponseDTO.ComplaintDTO createComplaint(Member member, ComplaintRequestDTO.CreateComplaintDTO createComplaintDTO);
}
