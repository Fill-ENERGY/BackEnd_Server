package com.example.template.domain.complaint.dto.request;

import com.example.template.domain.complaint.entity.Complaint;
import com.example.template.domain.complaint.entity.ComplaintType;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.station.entity.Station;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ComplaintRequestDTO {
    @Getter
    @Builder
    public static class CreateComplaintDTO {

        private Long stationId;
        @NotNull(message = "제목은 필수입니다.")
        private String title;
        @NotNull(message = "내용은 필수입니다.")
        private String content;
        @NotNull(message = "카테고리는 필수입니다.")
        private ComplaintType complaintType;

        private List<String> images;

        public static Complaint toEntity(CreateComplaintDTO createComplaintDTO, Station station, Member member) {
            return Complaint.builder()
                    .title(createComplaintDTO.getTitle())
                    .complaintType(createComplaintDTO.getComplaintType())
                    .content(createComplaintDTO.getContent())
                    .member(member)
                    .station(station).build();
        }
    }
}
