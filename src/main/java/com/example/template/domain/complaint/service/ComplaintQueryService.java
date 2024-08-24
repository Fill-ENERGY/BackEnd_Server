package com.example.template.domain.complaint.service;

import com.example.template.domain.complaint.dto.response.ComplaintResponseDTO;
import com.example.template.domain.member.entity.Member;

import java.util.List;

public interface ComplaintQueryService {
    ComplaintResponseDTO.getStationDTO getStationName(Long stationId);

    ComplaintResponseDTO.ComplaintDetailDTO getComplaintDetail(Member member, Long complaintId);

    List<ComplaintResponseDTO.ComplaintDTO> getComplaintList(Member member);
}
