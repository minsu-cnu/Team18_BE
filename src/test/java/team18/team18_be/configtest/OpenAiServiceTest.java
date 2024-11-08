package team18.team18_be.configtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team18.team18_be.Team18BeApplication;
import team18.team18_be.config.infrastructure.OpenAiService;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;

@SpringBootTest(classes = Team18BeApplication.class)
public class OpenAiServiceTest {

  private final RecruitmentRequest recruitmentRequest = new RecruitmentRequest(
      "주방 보조",                     // 직무 제목
      "중소기업",                     // 회사 규모
      "서울 강남구",                 // 근무 지역
      240000000L,                     // 급여
      "1년 이상",                    // 근무 기간
      "주 5일",                       // 근무일
      "풀타임",                       // 근무 형태
      "10:00 - 19:00",               // 근무 시간
      "신입 또는 경력",               // 요청 경력
      "한식, 양식 조리",              // 주요 업무
      "관련 전공 또는 자격증 소지",   // 자격 요건
      "경력 우대, 성실한 분",          // 우대 조건
      "김철수", //고용주 이름
      "학사마을 식당",// 회사 이름
      1L
  );
  @Autowired
  private OpenAiService openAiService;

  @Test
  void 한국어제목_베트남어로_바꾸기() throws JsonProcessingException {
    String result = openAiService.translateKoreanToVietnamese("스타벅스 충남대점에서 아르바이트생을 모집합니다.");
    System.out.println(result);
  }

  @Test
  void 받은_구인글_요약하고_번역() throws JsonProcessingException {
    String result = openAiService.summation(recruitmentRequest);
    System.out.println(result);
    System.out.println(openAiService.translateKoreanToVietnamese(result));
  }
}
