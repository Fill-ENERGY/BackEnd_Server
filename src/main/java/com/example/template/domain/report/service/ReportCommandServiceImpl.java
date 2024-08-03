package com.example.template.domain.report.service;

import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.dto.response.ReportResponseDTO;
import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.exception.ReportErrorCode;
import com.example.template.domain.report.exception.ReportException;
import com.example.template.domain.report.repository.ReportRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class ReportCommandServiceImpl implements ReportCommandService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    @Override
    public ReportResponseDTO.ReportDTO createReport(Long id, ReportRequestDTO.CreateReportDTO requestDTO) {
        Member member = memberRepository.findById(id).orElseThrow(()-> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Report report = requestDTO.toEntity(member);

        Report savedReport = reportRepository.save(report);
        return ReportResponseDTO.ReportDTO.from(savedReport);
    }

    @Override
    public Long deleteReport(Long memberId, ReportRequestDTO.UpdateReportDTO reportDTO) {
        Report report = reportRepository.findByMemberAndTargetIdAndReportType(memberId, reportDTO.getTargetId(), reportDTO.getReportType());
        if(report == null){
            throw new ReportException(ReportErrorCode.REPORT_NOT_FOUND);
        }
        reportRepository.delete(report);

        return report.getId();
    }
}
