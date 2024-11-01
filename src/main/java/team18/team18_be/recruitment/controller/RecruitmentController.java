package team18.team18_be.recruitment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team18.team18_be.auth.entity.User;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.recruitment.dto.request.RecruitmentRequest;
import team18.team18_be.recruitment.dto.response.RecruitmentResponse;
import team18.team18_be.recruitment.dto.response.RecruitmentResponseForCompany;
import team18.team18_be.recruitment.dto.response.RecruitmentSummationResponse;
import team18.team18_be.recruitment.service.RecruitmentService;

@Api(tags = {"구인글 관련 Controller"})
@RestController
@RequestMapping("/api/recruitments")
public class RecruitmentController {

  private final RecruitmentService recruitmentService;

  public RecruitmentController(RecruitmentService recruitmentService) {
    this.recruitmentService = recruitmentService;
  }

  @ApiOperation(value = "구인글 저장 메서드")
  @PostMapping
  public ResponseEntity<Void> saveRecruitment(
      @RequestBody RecruitmentRequest recruitmentRequest,
      @LoginUser User user
  ) throws JsonProcessingException {
    recruitmentService.saveRecruitment(recruitmentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @ApiOperation(value = "구인글 전체 조회 메서드")
  @GetMapping
  public ResponseEntity<List<RecruitmentSummationResponse>> getAllRecruitments(
      @RequestParam int page
  ) {
    int fixedPageSize = 4;
    Pageable pageable = PageRequest.of(page, fixedPageSize);
    return ResponseEntity.ok().body(recruitmentService.getAllRecruitment(pageable));
  }

  @ApiOperation(value = "구인글id로 조회 메서드")
  @GetMapping("/{postId}")
  public ResponseEntity<RecruitmentResponse> getRecruitments(
      @PathVariable Long postId
  ) {
    return ResponseEntity.ok()
        .body(recruitmentService.getRecruitmentResponseByRecruitmentId(postId));
  }

  @ApiOperation(value = "회사 별 구인글 조회 메서드")
  @GetMapping("/company/{companyId}")
  public ResponseEntity<List<RecruitmentResponseForCompany>> getAllRecruitmentByCompanyId(
      @PathVariable Long companyId
  ) {
    return ResponseEntity.ok()
        .body(recruitmentService.getRecruitmentResponseByCompanyId(companyId));
  }

  @ApiOperation(value = "구인글 마감 메서드")
  @GetMapping("/hiringClose/{recruitmentId}")
  public ResponseEntity<Void> setRecruitmentHiringFalse(
      @PathVariable Long recruitmentId
  ) {
    recruitmentService.setRecruitmentHiringFalse(recruitmentId);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "최근 올라온 구인글 순서대로 정렬")
  @GetMapping("/latestRegistration")
  public ResponseEntity<List<RecruitmentSummationResponse>> getAllRecruitmentsLatestRegistration(
      @RequestParam int page
  ) {
    int fixedPageSize = 4;
    Pageable pageable = PageRequest.of(page, fixedPageSize);
    return ResponseEntity.ok().body(recruitmentService.getAllRecruitment(pageable));
  }

  @ApiOperation(value = "급여 높은 순서대로 정리해서 전체 구인글 반환하는 메서드")
  @GetMapping("/salary")
  public ResponseEntity<List<RecruitmentSummationResponse>> getAllRecruitmentsSalary(
      @RequestParam int page
  ) {
    int fixedPageSize = 4;
    Pageable pageable = PageRequest.of(page, fixedPageSize);
    return ResponseEntity.ok().body(recruitmentService.getAllRecruitment(pageable));
  }

}
