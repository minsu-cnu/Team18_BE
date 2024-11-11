package team18.team18_be.resumetest;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team18.team18_be.Team18BeApplication;
import team18.team18_be.apply.entity.ApplicationForm;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplicationFormRepository;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.repository.RecruitmentRepository;
import team18.team18_be.recruitment.service.RecruitmentService;
import team18.team18_be.resume.dto.request.ResumeRequest;
import team18.team18_be.resume.dto.response.ResumeAndApplyResponse;
import team18.team18_be.resume.dto.response.ResumeResponse;
import team18.team18_be.resume.entity.Resume;
import team18.team18_be.resume.repository.ResumeRepository;
import team18.team18_be.resume.service.ResumeService;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.repository.CompanyRepository;

@SpringBootTest(classes = Team18BeApplication.class)
public class ResumeServiceTest {

  static final String NAME = "name";
  static final String EMAIL = "email";
  public static Long companyId;
  public static Long resumeId;
  public static Long applyId;
  public static User user;


  @Autowired
  ResumeService resumeService;
  @Autowired
  ResumeRepository resumeRepository;
  @Autowired
  AuthRepository authRepository;
  @Autowired
  ApplyRepository applyRepository;
  @Autowired
  RecruitmentService recruitmentService;
  @Autowired
  RecruitmentRepository recruitmentRepository;
  @Autowired
  ApplicationFormRepository applicationFormRepository;
  @Autowired
  CompanyRepository companyRepository;

  @BeforeEach
  public void setup() throws JsonProcessingException {
    user = new User(NAME, EMAIL, "first");
    authRepository.save(user);
    Company company = new Company("MyCompany", "Tech", "MyBrand", 1000000L, "image", user);
    companyId = companyRepository.save(company).getId();

    resumeId = resumeService.saveResume(
        new ResumeRequest("김민지",
            "서울특별시 강남구 테헤란로 123",
            "010-1234-5678",
            "3년 이상의 백엔드 개발 경력",
            "한국어 능력 상",
            "저는 효율적인 시스템을 구축하고, 사용자의 경험을 향상시키는 것을 목표로 하는 백엔드 개발자입니다. 다양한 기술 스택을 활용하여 문제를 해결하며, 팀워크와 협업을 중요시합니다.")
        , user
    );

    Long recruitmentId = recruitmentService.saveRecruitment(new RecruitmentRequest(
        "해외 영업 전문가 모집", "200명 이상의 직원", "부산, 대한민국",
        120000000L, "정규직", "주 5일", "상근", "오전 8시부터 오후 5시까지",
        "경력 10년 이상", "무역 회사", "국제 무역 및 영업 관련 전공",
        "영어 능통 및 협상 기술", "이민호", "글로벌 트레이드", companyId));

    Apply apply = new Apply("status", user, recruitmentRepository.findById(recruitmentId).get());
    applyId = applyRepository.save(apply).getId();
    applicationFormRepository.save(
        new ApplicationForm("김민지", "충남대학교", "01033333333", "한국어 실력을 늘리고자 지원했습니다.", apply));


  }

  @Test
  @Transactional
  @DisplayName("이력서 저장하기")
  public void saveResumeTest() {
    Resume resume = resumeRepository.findById(resumeId).get();
    assertThat(resume.getApplicantName()).isEqualTo("김민지");
  }

  @Test
  @Transactional
  @DisplayName("사용자 별 이력서 찾기")
  public void findResumeByEmployeeIdTest() {
    ResumeResponse resumeResponse = resumeService.findResumeByEmployee(user);
    assertThat(resumeResponse.applicantName()).isEqualTo("김민지");
  }

  @Test
  @Transactional
  @DisplayName("이력서 id로 이력서 찾고, 지원서 내용 추가")
  public void findResumeByIdTest(){
    ResumeAndApplyResponse resumeAndApplyResponse = resumeService.findResumeById(resumeId, applyId);
    assertThat(resumeAndApplyResponse.applicantName()).isEqualTo("김민지");
    assertThat(resumeAndApplyResponse.motivation()).isEqualTo("한국어 실력을 늘리고자 지원했습니다.");
  }


}
