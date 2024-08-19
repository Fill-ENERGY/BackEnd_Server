package com.example.template.domain.complaint.dto.response;

import com.example.template.domain.complaint.entity.Complaint;
import com.example.template.domain.complaint.entity.ComplaintImg;
import com.example.template.domain.complaint.entity.ComplaintType;
import com.example.template.domain.station.entity.Station;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ComplaintResponseDTO {

    @Builder
    @Getter
    public static class getStationDTO{
        private String stationName;

        public static getStationDTO from(Station station) {
            return getStationDTO.builder()
                    .stationName(station.getName()).build();
        }
    }

    @Builder
    @Getter
    public static class ComplaintDTO{
        private Long complaintId;
        private String image;
        private String name;
        private String content;
        private String complaintType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ComplaintDTO from(Complaint complaint, ComplaintImg complaintImg) {
            return ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .image(complaintImg != null ? complaintImg.getImgUrl() : null) // 이미지가 null이 아닐 경우 URL 반환, null이면 null 반환
                    .name(complaint.getStation().getName())
                    .content(complaint.getContent())
                    .complaintType(complaint.getComplaintType().getDescription())
                    .createdAt(complaint.getCreatedAt())
                    .updatedAt(complaint.getUpdatedAt())
                    .build();
        }
    }
    @Builder
    @Getter
    public static class ComplaintDetailDTO {
        private Long complaintId;
        private String name;
        private String address; //도로명
        private String streetNumber;//지번
        private String institutionPhone; //번호
        private String title;
        private String content;
        private String complaintType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> images;

        public static ComplaintDetailDTO from(Complaint complaint, List<ComplaintImg> complaintImgList){
            List<String> complaintImgURL = complaintImgList.stream()
                    .map(ComplaintImg::getImgUrl)
                    .collect(Collectors.toList());
            return ComplaintDetailDTO.builder()
                    .complaintId(complaint.getId())
                    .name(complaint.getStation().getName())
                    .address(complaint.getStation().getAddress())
                    .streetNumber(complaint.getStation().getStreetNumber())
                    .institutionPhone(complaint.getStation().getInstitutionPhone())
                    .title(complaint.getTitle())
                    .content(complaint.getContent())
                    .complaintType(complaint.getComplaintType().getDescription())
                    .images(complaintImgURL)
                    .createdAt(complaint.getCreatedAt())
                    .updatedAt(complaint.getUpdatedAt())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class ComplaintImgDTO {
        private List<String> images;
    }
}
