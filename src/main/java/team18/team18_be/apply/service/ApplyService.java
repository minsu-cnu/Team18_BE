package team18.team18_be.apply.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import team18.team18_be.apply.ApplyStatusEnum.ApplyStatus;
import team18.team18_be.apply.dto.request.ApplicationFormRequest;
import team18.team18_be.apply.dto.response.ApplicationFormResponse;
import team18.team18_be.apply.dto.response.ApplierPerRecruitmentResponse;
import team18.team18_be.apply.dto.response.MandatoryResponse;
import team18.team18_be.apply.dto.response.RecruitmentsOfApplierResponse;
import team18.team18_be.apply.entity.ApplicationForm;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplicationFormRepository;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.repository.RecruitmentRepository;
import team18.team18_be.resume.entity.Resume;
import team18.team18_be.resume.repository.ResumeRepository;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.entity.ForeignerInformation;
import team18.team18_be.userInformation.repository.CompanyRepository;
import team18.team18_be.userInformation.repository.ForeignerInformationRepository;

@Service
public class ApplyService {

  private final ApplicationFormRepository applicationFormRepository;
  private final ApplyRepository applyRepository;
  private final RecruitmentRepository recruitmentRepository;
  private final ResumeRepository resumeRepository;
  private final CompanyRepository companyRepository;
  private final ForeignerInformationRepository foreignerInformationRepository;


  public ApplyService(ApplicationFormRepository applicationFormRepository,
      ApplyRepository applyRepository, RecruitmentRepository recruitmentRepository,
      ResumeRepository resumeRepository, CompanyRepository companyRepository,
      ForeignerInformationRepository foreignerInformationRepository) {
    this.applicationFormRepository = applicationFormRepository;
    this.applyRepository = applyRepository;
    this.recruitmentRepository = recruitmentRepository;
    this.resumeRepository = resumeRepository;
    this.companyRepository = companyRepository;
    this.foreignerInformationRepository = foreignerInformationRepository;
  }

  public Long createApplicationForm(ApplicationFormRequest applicationFormRequest,
      Long recruitmentId, User user) {
    ApplyStatus status = ApplyStatus.REVIEWING_APPLICATION;
    Recruitment recruitment = findRecruitment(recruitmentId);
    Apply apply = new Apply(status.getKoreanName(), user, recruitment);
    Apply savedApply = applyRepository.save(apply);
    ApplicationForm applicationForm = new ApplicationForm(applicationFormRequest.name(),
        applicationFormRequest.address(), applicationFormRequest.phoneNumber(),
        applicationFormRequest.motivation(), savedApply);
    applicationFormRepository.save(applicationForm);
    return savedApply.getId();
  }

  public ApplicationFormResponse findApplicationForm(Long applyId) {
    Apply apply = applyRepository.findById(applyId)
        .orElseThrow(() -> new NoSuchElementException("해당 지원서가 없습니다"));
    ApplicationForm applicationForm = applicationFormRepository.findByApply(apply)
        .orElseThrow(() -> new NoSuchElementException("해당 지원서가 없습니다."));
    ApplicationFormResponse applicationFormResponse = new ApplicationFormResponse(
        applicationForm.getName(), applicationForm.getAddress(), applicationForm.getPhoneNumber(),
        applicationForm.getMotivation());
    return applicationFormResponse;
  }


  public List<ApplierPerRecruitmentResponse> searchApplicant(Long recruitmentId, User user) {
    Recruitment recruitment = findRecruitment(recruitmentId);

    return applyRepository.findByRecruitment(recruitment)
        .orElseThrow(() -> new NoSuchElementException("해당하는 지원이 없습니다."))
        .stream()
        .map(this::createApplierPerRecruitmentResponse)
        .collect(Collectors.toList());
  }

  private ApplierPerRecruitmentResponse createApplierPerRecruitmentResponse(Apply apply) {
    User applicantUser = apply.getUser();
    Resume resume = resumeRepository.findByUser(applicantUser); //그 지원자의 이력서 가져오기
    return new ApplierPerRecruitmentResponse(
        applicantUser.getId(), applicantUser.getName(), resume.getResumeId(), apply.getId(),
        "베트남", resume.getKorean()
    );
  }

  public List<RecruitmentsOfApplierResponse> searchMyAppliedRecruitments(User user) {
    return applyRepository.findByUser(user)
        .orElseThrow(() -> new NoSuchElementException("해당하는 지원이 없습니다."))
        .stream()
        .map(this::createMyAppliedRecruitments)
        .collect(Collectors.toList());
  }

  private RecruitmentsOfApplierResponse createMyAppliedRecruitments(Apply apply) {
    //지원자가 지원한 공고글
    Recruitment recruitment = findRecruitment(apply.getRecruitment().getRecruitmentId());
    //공고글의 회사
    Company company = companyRepository.findById(recruitment.getCompany().getId())
        .orElseThrow(() -> new NoSuchElementException("해당 회사가 없습니다."));
    RecruitmentsOfApplierResponse recruitmentsOfApplierResponse = new RecruitmentsOfApplierResponse(
        recruitment.getRecruitmentId(), company.getLogoImage(), recruitment.getKoreanTitle(),
        recruitment.getArea(), apply.getStatus(), apply.getId());
    return recruitmentsOfApplierResponse;
  }

  public MandatoryResponse checkMandatory(User user) {
    ForeignerInformation foreignerInformation = foreignerInformationRepository.findByUser(user)
        .orElseThrow(() -> new NoSuchElementException("해당 외국인 정보가 없습니다."));
    Resume resume = resumeRepository.findByUser(user);
    boolean visaExistence = checkNull(foreignerInformation.getForeignerIdNumber());
    boolean resumeExistence = checkNull(foreignerInformation.getVisaGenerateDate());
    boolean foreignerIdNumberExistence = checkNull(resume);
    MandatoryResponse mandatoryResponse = new MandatoryResponse(resumeExistence, visaExistence,
        foreignerIdNumberExistence);
    return mandatoryResponse;
  }

  private Recruitment findRecruitment(Long recruitmentId) {
    Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
        .orElseThrow(() -> new NoSuchElementException("해당되는 구인글이 없습니다."));
    return recruitment;
  }

  private boolean checkNull(Object object) {
    return object != null;
  }

}
