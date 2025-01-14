package team18.team18_be.recruitment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team18.team18_be.apply.ApplyStatusEnum.ApplyStatus;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.config.infrastructure.OpenAiService;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.dto.response.PageDto;
import team18.team18_be.recruitment.dto.response.RecruitmentAllResponse;
import team18.team18_be.recruitment.dto.response.RecruitmentResponse;
import team18.team18_be.recruitment.dto.response.RecruitmentResponseForCompany;
import team18.team18_be.recruitment.dto.response.RecruitmentSummationResponse;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.entity.RecruitmentContent;
import team18.team18_be.recruitment.mapper.RecruitmentMapper;
import team18.team18_be.recruitment.repository.RecruitmentContentRepository;
import team18.team18_be.recruitment.repository.RecruitmentRepository;
import team18.team18_be.userInformation.repository.CompanyRepository;

@Service
public class RecruitmentService {

  private final RecruitmentRepository recruitmentRepository;
  private final RecruitmentContentRepository recruitmentContentRepository;
  private final CompanyRepository companyRepository;
  private final OpenAiService openAiService;
  private final RecruitmentMapper recruitmentMapper;
  private final ApplyRepository applyRepository;

  public RecruitmentService(RecruitmentRepository recruitmentRepository,
      RecruitmentContentRepository recruitmentContentRepository, OpenAiService openAiService,
      CompanyRepository companyRepository, RecruitmentMapper recruitmentMapper,
      ApplyRepository applyRepository) {
    this.recruitmentRepository = recruitmentRepository;
    this.recruitmentContentRepository = recruitmentContentRepository;
    this.openAiService = openAiService;
    this.companyRepository = companyRepository;
    this.recruitmentMapper = recruitmentMapper;
    this.applyRepository = applyRepository;
  }

  public Long saveRecruitment(RecruitmentRequest recruitmentRequest)
      throws JsonProcessingException {
    String koreanTitle = recruitmentRequest.title();
    String vietnameseTitle = openAiService.translateKoreanToVietnamese(koreanTitle);

    String koreanDetailedDescription = openAiService.summation(recruitmentRequest);
    String vietnameseDetailedDescription = openAiService.translateKoreanToVietnamese(
        koreanDetailedDescription);
    RecruitmentContent recruitmentContent = recruitmentContentRepository.save(
        new RecruitmentContent(koreanDetailedDescription, vietnameseDetailedDescription));
    return recruitmentRepository.save(
        recruitmentMapper.toRecruitment(koreanTitle, vietnameseTitle, recruitmentRequest,
            recruitmentContent, companyRepository.findById(recruitmentRequest.companyId())
                .orElseThrow(() -> new NoSuchElementException("해당하는 회사가 존재하지 않습니다.")), true,
            new Date()
        )).getRecruitmentId();

  }

  public RecruitmentAllResponse getAllRecruitment(Pageable pageable) {
    Page<Recruitment> recruitments = recruitmentRepository.findAllByHiringTrue(pageable);
    List<RecruitmentSummationResponse> recruitmentSummationResponseList =
        recruitments.stream()
            .map(recruitment -> new RecruitmentSummationResponse(
                recruitment.getRecruitmentId(),
                recruitment.getCompany().getLogoImage(),
                recruitment.getKoreanTitle(),
                recruitment.getVietnameseTitle(),
                recruitment.getCompanyName(),
                recruitment.getSalary(),
                recruitment.getArea(),
                recruitment.getWorkHours()
            ))
            .collect(Collectors.toList());
    int totalPage = recruitments.getTotalPages();
    return new RecruitmentAllResponse(recruitmentSummationResponseList, new PageDto(totalPage));
  }

  public RecruitmentAllResponse getAllRecruitmentAndSortBySalary(Pageable pageable) {
    Page<Recruitment> recruitments = recruitmentRepository.findAllByHiringTrueOrderBySalaryDesc(
        pageable);
    List<RecruitmentSummationResponse> recruitmentSummationResponseList =
        recruitments.stream()
            .map(recruitment -> new RecruitmentSummationResponse(
                recruitment.getRecruitmentId(),
                recruitment.getCompany().getLogoImage(),
                recruitment.getKoreanTitle(),
                recruitment.getVietnameseTitle(),
                recruitment.getCompanyName(),
                recruitment.getSalary(),
                recruitment.getArea(),
                recruitment.getWorkHours()
            ))
            .collect(Collectors.toList());
    int totalPage = recruitments.getTotalPages();
    return new RecruitmentAllResponse(recruitmentSummationResponseList, new PageDto(totalPage));
  }

  public RecruitmentAllResponse getAllRecruitmentAndSortByDate(Pageable pageable) {
    Page<Recruitment> recruitments = recruitmentRepository.findAllByHiringTrueOrderByUploadDateDesc(
        pageable);
    List<RecruitmentSummationResponse> recruitmentSummationResponseList =
        recruitments.stream()
            .map(recruitment -> new RecruitmentSummationResponse(
                recruitment.getRecruitmentId(),
                recruitment.getCompany().getLogoImage(),
                recruitment.getKoreanTitle(),
                recruitment.getVietnameseTitle(),
                recruitment.getCompanyName(),
                recruitment.getSalary(),
                recruitment.getArea(),
                recruitment.getWorkHours()
            ))
            .collect(Collectors.toList());
    int totalPage = recruitments.getTotalPages();
    return new RecruitmentAllResponse(recruitmentSummationResponseList, new PageDto(totalPage));
  }

  public RecruitmentResponse getRecruitmentResponseByRecruitmentId(Long userId) {
    Recruitment recruitment = recruitmentRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 이력서가 존재하지 않습니다."));
    return recruitmentMapper.toRecruitmentResponse(recruitment,
        recruitment.getRecruitmentContent(), recruitment.getCompany().getLogoImage());
  }

  public List<RecruitmentResponseForCompany> getRecruitmentResponseByCompanyId(Long companyId) {
    List<Recruitment> recruitments = recruitmentRepository.findByCompany(
        companyRepository.findById(companyId));
    return recruitments.stream()
        .map(recruitment -> new RecruitmentResponseForCompany(
            recruitment.getRecruitmentId(),
            recruitment.getCompany().getLogoImage(),
            recruitment.getKoreanTitle(),
            recruitment.getVietnameseTitle(),
            recruitment.getCompanyName(),
            recruitment.getSalary(),
            recruitment.getArea(),
            recruitment.getHiring()
        ))
        .collect(Collectors.toList());
  }

  public void setApplyHiringFalse(Recruitment recruitment) {
    List<Apply> applyList = applyRepository.findByRecruitment(recruitment)
        .orElseThrow(() -> new NoSuchElementException("해당하는 지원이 존재하지 않습니다."));
    ApplyStatus status = ApplyStatus.HIRING_CLOSED;
    for (Apply apply : applyList) {
      applyRepository.save(new Apply(apply.getId(), status.getKoreanName(), apply.getUser(),
          apply.getRecruitment()));
    }
  }

  public void setRecruitmentHiringFalse(Long recruitmentId) {
    Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
        .orElseThrow(() -> new NoSuchElementException("해당하는 회사가 존재하지 않습니다."));
    recruitment.setHiring(false);
    setApplyHiringFalse(recruitment);
    recruitmentRepository.save(recruitment);
  }

}
