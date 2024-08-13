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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    public static class ComplaintDTO {
        private Long complaintId;
        private String title;
        private String content;
        private ComplaintType complaintType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> images;

        public static ComplaintDTO from(Complaint complaint, List<ComplaintImg> complaintImgList){
            List<String> complaintImgURL = complaintImgList.stream()
                    .map(ComplaintImg::getImgUrl)
                    .collect(Collectors.toList());

            return ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .complaintType(complaint.getComplaintType())
                    .title(complaint.getTitle())
                    .content(complaint.getContent())
                    .images(complaintImgURL)
                    .createdAt(complaint.getCreatedAt())
                    .updatedAt(complaint.getUpdatedAt())
                    .build();
        }

        //목록조회를 위해 이미지가 없는 버전
        public static ComplaintDTO from(Complaint complaint){
            return ComplaintDTO.builder()
                    .complaintId(complaint.getId())
                    .complaintType(complaint.getComplaintType())
                    .title(complaint.getTitle())
                    .content(complaint.getContent())
                    .createdAt(complaint.getCreatedAt())
                    .updatedAt(complaint.getUpdatedAt())
                    .build();
        }

        public static List<ComplaintDTO> from(List<Complaint> complaints) {
            return complaints.stream()
                    .map(ComplaintDTO::from)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    public static class ComplaintImgDTO {
        private List<String> images;
    }
}
