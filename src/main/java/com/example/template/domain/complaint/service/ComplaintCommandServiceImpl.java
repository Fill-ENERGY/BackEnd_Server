package com.example.template.domain.complaint.service;

import com.example.template.domain.complaint.dto.request.ComplaintRequestDTO;
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
import com.example.template.global.config.aws.S3Manager;
import com.example.template.global.util.s3.entity.Uuid;
import com.example.template.global.util.s3.repository.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintCommandServiceImpl implements ComplaintCommandService{

    private final ComplaintImgRepository complaintImgRepository;
    private final S3Manager s3Manager;
    private final UuidRepository uuidRepository;
    private final StationRepository stationRepository;
    private final ComplaintRepository complaintRepository;
    @Override
    public ComplaintResponseDTO.ComplaintImgDTO uploadImages(List<MultipartFile> images) {
        List<String> keyNames = new ArrayList<>();
        List<Uuid> uuids = new ArrayList<>();

        // UUID 생성 및 키 이름 생성
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                Uuid savedUuid = Uuid.builder().uuid(uuid).build();
                uuids.add(savedUuid);
                keyNames.add(s3Manager.generateComplaintKeyName(savedUuid));
            }
        }
        // UUID  저장
        uuidRepository.saveAll(uuids);

        List<String> imageUrls = s3Manager.uploadFiles(keyNames, images);

        // 이미지 엔티티 생성 및 저장 (연결x)
        List<ComplaintImg> complaintImgs = imageUrls.stream()
                .map(url -> ComplaintImg.builder().imgUrl(url).build())
                .toList();
        complaintImgRepository.saveAll(complaintImgs);

        // ComplaintImgUrl 생성 및 반환
        return ComplaintResponseDTO.ComplaintImgDTO.builder()
                .images(imageUrls)
                .build();
    }

    @Override
    public ComplaintResponseDTO.ComplaintDetailDTO createComplaint(Member member, ComplaintRequestDTO.CreateComplaintDTO createComplaintDTO) {
        Station station = stationRepository.findById(createComplaintDTO.getStationId()).
                orElseThrow(() -> new StationException(StationErrorCode.NOT_FOUND));
        Complaint complaint = ComplaintRequestDTO.CreateComplaintDTO.toEntity(createComplaintDTO, station, member);

        List<ComplaintImg> complaintImgs = null;
        if (createComplaintDTO.getImages() != null && !createComplaintDTO.getImages().isEmpty()) {
            complaintImgs = complaintImgRepository.findAllByImgUrlIn(createComplaintDTO.getImages());

            // S3에 등록되지 않은 이미지를 가지고 접근
            if (complaintImgs.size() != createComplaintDTO.getImages().size()) {
                throw new ComplaintException(ComplaintErrorCode.INVALID_IMAGE_URLS);
            }
            complaintImgs.forEach(img -> img.updateComplaint(complaint));
        }

        Complaint savedComplaint = complaintRepository.save(complaint);
        return ComplaintResponseDTO.ComplaintDetailDTO.from(savedComplaint, complaintImgs);

    }


}
