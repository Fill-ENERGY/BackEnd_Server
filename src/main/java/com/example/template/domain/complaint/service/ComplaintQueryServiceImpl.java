package com.example.template.domain.complaint.service;

import com.example.template.domain.complaint.dto.response.ComplaintResponseDTO;
import com.example.template.domain.complaint.entity.Complaint;
import com.example.template.domain.complaint.entity.ComplaintImg;
import com.example.template.domain.complaint.exception.ComplaintErrorCode;
import com.example.template.domain.complaint.exception.ComplaintException;
import com.example.template.domain.complaint.repository.ComplaintImgRepository;
import com.example.template.domain.complaint.repository.ComplaintRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Station;
import com.example.template.domain.station.exception.StationErrorCode;
import com.example.template.domain.station.exception.StationException;
import com.example.template.domain.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ComplaintQueryServiceImpl implements ComplaintQueryService{

    private final StationRepository stationRepository;
    private final ComplaintRepository complaintRepository;
    private final ComplaintImgRepository complaintImgRepository;

    @Override
    public ComplaintResponseDTO.getStationDTO getStationName(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(()->new StationException(StationErrorCode.NOT_FOUND));
        return ComplaintResponseDTO.getStationDTO.from(station);
    }

    @Override
    public ComplaintResponseDTO.ComplaintDTO getComplaintDetail(Member member, Long complaintId) {
        Complaint complaint = complaintRepository.findByIdAndMember(complaintId, member);
        if(complaint == null){
            throw new ComplaintException(ComplaintErrorCode.COMPLAINT_NOT_FOUND);
        }
        List<ComplaintImg> complaintImgList = complaintImgRepository.findAllByComplaintId(complaint.getId());
        if(complaintImgList == null){
            throw new ComplaintException(ComplaintErrorCode.INVALID_IMAGE_URLS);
        }
        return ComplaintResponseDTO.ComplaintDTO.from(complaint);
    }

    @Override
    public List<ComplaintResponseDTO.ComplaintDTO> getComplaintList(Member member) {
        List<Complaint> complaintList = complaintRepository.findAllByMemberId(member.getId());
        return ComplaintResponseDTO.ComplaintDTO.from(complaintList);
    }
}
