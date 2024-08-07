package com.example.template.domain.report.service;

import com.example.template.domain.board.repository.BoardRepository;
import com.example.template.domain.board.repository.CommentRepository;
import com.example.template.domain.member.entity.Member;
import com.example.template.domain.member.exception.MemberErrorCode;
import com.example.template.domain.member.exception.MemberException;
import com.example.template.domain.member.repository.MemberRepository;
import com.example.template.domain.report.dto.request.ReportRequestDTO;
import com.example.template.domain.report.dto.response.ReportResponseDTO;
import com.example.template.domain.report.entity.Report;
import com.example.template.domain.report.entity.ReportContent;
import com.example.template.domain.report.entity.ReportType;
import com.example.template.domain.report.exception.ReportErrorCode;
import com.example.template.domain.report.exception.ReportException;
import com.example.template.domain.report.repository.ReportContentRepository;
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
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReportContentRepository reportContentRepository;
    @Override
    public ReportResponseDTO.ReportDTO createReport(Long id, ReportRequestDTO.CreateReportDTO requestDTO) {
        Member member = memberRepository.findById(id).orElseThrow(()-> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        Report report = ReportRequestDTO.toEntity(member, requestDTO);

        //유효성 검사,,,
        switch (requestDTO.getReportCategory()){
            case MEMBER :
                memberRepository.findById(requestDTO.getTargetId()).orElseThrow(()-> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));
                break;
            case BOARD:
                boardRepository.findById(requestDTO.getTargetId()).orElseThrow(()->new ReportException(ReportErrorCode.REPORT_NOT_FOUND));
                break;
            case COMMENT:
                commentRepository.findById(requestDTO.getTargetId()).orElseThrow(()-> new ReportException(ReportErrorCode.REPORT_NOT_FOUND));
                break;
        }
        Report savedReport = reportRepository.save(report);

        if(requestDTO.getReportType() == ReportType.OTHER){
            ReportContent reportContent = ReportRequestDTO.toEntity(savedReport, requestDTO);
            ReportContent savedReportContent = reportContentRepository.save(reportContent);

            return ReportResponseDTO.ReportDTO.from(savedReport, savedReportContent);
        }

        return ReportResponseDTO.ReportDTO.from(savedReport);
    }

    @Override
    public Long deleteReport(Long memberId, ReportRequestDTO.DeleteReportDTO reportDTO) {
        Report report = reportRepository.findByMemberAndTargetIdAndReportCategory(memberId, reportDTO.getTargetId(), reportDTO.getReportCategory());
        if(report == null){
            throw new ReportException(ReportErrorCode.REPORT_NOT_FOUND);
        }
        if(report.getReportType() == ReportType.OTHER){
            reportContentRepository.deleteByReportId(report.getId());
        }
        reportRepository.delete(report);

        return report.getId();
    }
}
