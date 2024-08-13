package com.example.template.domain.complaint.entity;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ComplaintImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_img_id", nullable = false)
    private Long id;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;  // 사진 경로

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    public void updateComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
