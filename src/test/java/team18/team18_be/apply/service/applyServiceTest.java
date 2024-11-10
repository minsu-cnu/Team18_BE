package team18.team18_be.apply.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import team18.team18_be.apply.ApplyStatusEnum.ApplyStatus;
import team18.team18_be.apply.dto.request.ApplicationFormRequest;
import team18.team18_be.apply.dto.response.ApplierPerRecruitmentResponse;
import team18.team18_be.apply.dto.response.RecruitmentsOfApplierResponse;
import team18.team18_be.apply.entity.ApplicationForm;
import team18.team18_be.apply.entity.Apply;
import team18.team18_be.apply.repository.ApplicationFormRepository;
import team18.team18_be.apply.repository.ApplyRepository;
import team18.team18_be.auth.entity.User;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.entity.RecruitmentContent;
import team18.team18_be.recruitment.repository.RecruitmentRepository;
import team18.team18_be.resume.entity.Resume;
import team18.team18_be.resume.repository.ResumeRepository;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.repository.CompanyRepository;

public class applyServiceTest {

  @Mock
  private ApplyRepository applyRepository;

  @Mock
  private ApplicationFormRepository applicationFormRepository;
  @Mock
  private RecruitmentRepository recruitmentRepository;
  @Mock
  private ResumeRepository resumeRepository;
  @Mock
  private CompanyRepository companyRepository;
  @InjectMocks
  private ApplyService applyService;
  private User employee;
  private User employer;
  private Company company;
  private RecruitmentContent recruitmentContent;
  private Recruitment recruitment;
  private Apply apply;
  private Resume resume;
  private ApplicationForm applicationForm;


  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    employee = new User(1L, "홍길동", "abcd@naver.com", "2");
    employer = new User(2L, "A사장", "employer@naver.com", "1");
    company = new Company(1L, "A", "Food", "Nobrand", 10000L, "aaaa.logoimage.src", employer);
    recruitmentContent = new RecruitmentContent("koreanDetailedDescription",
        "vietnameseDetailedDescription");
    recruitment = new Recruitment("koreanTitle", "vietnameseTitle", "companySize",
        "area", 100000000L,
        "workDuration", "workDays", "workType", "workHours", "requestedCareer", "majorBusiness",
        "eligibilityCriteria", "preferredConditions", "employerName", "companyName", company,
        recruitmentContent);
    resume = new Resume(1L, "홍길동", "123 street", "01012345678", "cooker", "good", "myIntroduction",
        employee);
    apply = new Apply(1L, ApplyStatus.REVIEWING_APPLICATION.getKoreanName(), employee,
        recruitment);
    applicationForm = new ApplicationForm("홍길동", "123 street", "01012345678", "myMotivation",
        apply);
  }

  @Test
  public void testCreateApplicationForm() {
    // Given
    ApplicationFormRequest request = new ApplicationFormRequest("홍길동", "123 Street","010333333333",
        "my_motivation");
    Long recruitmentId = 1L;

    when(applyRepository.save(any(Apply.class))).thenReturn(
        apply);
    when(applicationFormRepository.save(any(ApplicationForm.class))).thenReturn(
        applicationForm);
    when(recruitmentRepository.findById(recruitmentId)).thenReturn(
        Optional.of(recruitment));

    // When
    Long result = applyService.createApplicationForm(request, recruitmentId, employee);

    // Then
    assertNotNull(result);
    assertEquals(apply.getId(), result);
    verify(applyRepository).save(any(Apply.class));
    verify(applicationFormRepository).save(any(ApplicationForm.class));
  }

  @Test
  public void testSearchApplicant() {
    //given
    Long recruitmentId = 1L;
    List<Apply> applies = Stream.of(apply).collect(Collectors.toList());
    when(recruitmentRepository.findById(recruitmentId)).thenReturn(
        Optional.ofNullable(recruitment));
    when(applyRepository.findByRecruitment(recruitment)).thenReturn(
        Optional.ofNullable(applies));
    when(resumeRepository.findByUser(employee)).thenReturn(resume);

    //when
    List<ApplierPerRecruitmentResponse> responses = applyService.searchApplicant(
        recruitmentId, employer);

    //then
    assertNotNull(responses);
    assertEquals(applies.size(), responses.size());
  }

  @Test
  public void testSearchMyAppliedRecruitments() {
    //given
    List<Apply> applies = Stream.of(apply).toList();
    when(applyRepository.findByUser(employee)).thenReturn(Optional.of(applies));
    when(recruitmentRepository.findById(recruitment.getRecruitmentId())).thenReturn(
        Optional.ofNullable(recruitment));
    when(companyRepository.findById(recruitment.getCompany().getId())).thenReturn(
        Optional.ofNullable(company));

    //when
    List<RecruitmentsOfApplierResponse> responses = applyService.searchMyAppliedRecruitments(
        employee);

    //then
    assertNotNull(responses);
    assertEquals(apply.getId(), responses.get(0).applyId());
    assertEquals(company.getLogoImage(), responses.get(0).image());
  }

}
