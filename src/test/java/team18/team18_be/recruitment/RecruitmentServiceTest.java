package team18.team18_be.recruitment;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import team18.team18_be.Team18BeApplication;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.dto.response.RecruitmentAllResponse;
import team18.team18_be.recruitment.dto.response.RecruitmentSummationResponse;
import team18.team18_be.recruitment.entity.Recruitment;
import team18.team18_be.recruitment.repository.RecruitmentRepository;
import team18.team18_be.recruitment.service.RecruitmentService;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.repository.CompanyRepository;

@SpringBootTest(classes = Team18BeApplication.class)
public class RecruitmentServiceTest {

  static final String NAME = "name";
  static final String EMAIL = "email";

  public static Long companyId;
  public static Long firstRecruitmentId;
  public static Long secondRecruitmentId;
  public static Long thirdRecruitmentId;



  @Autowired
  RecruitmentRepository recruitmentRepository;
  @Autowired
  RecruitmentService recruitmentService;
  @Autowired
  CompanyRepository companyRepository;
  @Autowired
  AuthRepository authRepository;

  @BeforeEach
  public void setup() throws JsonProcessingException {
    User user = new User(NAME, EMAIL, "first");
    authRepository.save(user);
    Company company = new Company("MyCompany", "Tech", "MyBrand", 1000000L, "image", user);
    companyId = companyRepository.save(company).getId();

    firstRecruitmentId = recruitmentService.saveRecruitment(new RecruitmentRequest(
        "초보자를 위한 요리사 모집", "30명 이상의 직원", "제주, 대한민국",
        25000000L, "계약직", "주 6일", "파트타임", "오전 7시부터 오후 3시까지",
        "경력 1년 이상", "지역 맛집", "요리 관련 자격증 소지자 우대",
        "팀워크와 성실함", "정하윤", "제주 맛집", companyId));

    secondRecruitmentId = recruitmentService.saveRecruitment(new RecruitmentRequest(
        "프리랜서 웹 개발자 모집", "10명 이하의 직원", "서울, 대한민국",
        100000000L, "프리랜서", "주 3일", "원격 근무", "유연한 근무 시간",
        "경력 7년 이상", "스타트업", "풀스택 개발 경험 필수",
        "문제 해결 능력 및 주도성", "김재훈", "테크이노베이션", companyId));

    thirdRecruitmentId = recruitmentService.saveRecruitment(new RecruitmentRequest(
        "해외 영업 전문가 모집", "200명 이상의 직원", "부산, 대한민국",
        120000000L, "정규직", "주 5일", "상근", "오전 8시부터 오후 5시까지",
        "경력 10년 이상", "무역 회사", "국제 무역 및 영업 관련 전공",
        "영어 능통 및 협상 기술", "이민호", "글로벌 트레이드", companyId));
  }

  @Test
  @Transactional
  @DisplayName("구인글 저장")
  public void saveRecruitmentTest() {
    Recruitment recruitment = recruitmentRepository.findById(firstRecruitmentId).get();
    assertThat(recruitment.getArea()).isEqualTo("제주, 대한민국");
  }

  @Test
  @Transactional
  @DisplayName("구인글 전체조회")
  public void findAllRecruitmentTest(){
    Pageable pageable = PageRequest.of(0, 5);

    RecruitmentAllResponse recruitmentAllResponse = recruitmentService.getAllRecruitment(
        pageable);
    assertThat(recruitmentAllResponse.content().size()).isEqualTo(3);
  }

  @Test
  @Transactional
  @DisplayName("구인글 급여 순서대로 전체조회")
  public void findAllRecruitmentBySalaryTest() throws JsonProcessingException {
    Pageable pageable = PageRequest.of(0, 5);

    RecruitmentAllResponse recruitmentAllResponse = recruitmentService.getAllRecruitmentAndSortBySalary(
        pageable);

    assertThat(recruitmentAllResponse.content().get(0).recruitmentId()).isEqualTo(thirdRecruitmentId);
    assertThat(recruitmentAllResponse.content().get(1).recruitmentId()).isEqualTo(secondRecruitmentId);
    assertThat(recruitmentAllResponse.content().get(2).recruitmentId()).isEqualTo(firstRecruitmentId);
  }

}
