package team18.team18_be.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team18.team18_be.apply.dto.response.ApplierPerRecruitmentResponse;
import team18.team18_be.apply.dto.response.MandatoryResponse;
import team18.team18_be.apply.dto.response.RecruitmentsOfApplierResponse;
import team18.team18_be.apply.service.ApplyService;
import team18.team18_be.auth.entity.User;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.apply.dto.request.ApplicationFormRequest;

@RestController
@RequestMapping("/api/application")
@Tag(name = "지원 API")
public class ApplyController {

  private final ApplyService applyService;

  public ApplyController(ApplyService applyService) {
    this.applyService = applyService;
  }

  @Operation(summary = "지원서 등록")
  @PostMapping("/{recruitmentId}")
  public ResponseEntity<Void> createApplicationForm(
      @RequestBody ApplicationFormRequest applicationFormRequest, @PathVariable Long recruitmentId,
      @LoginUser User user) {
    Long applicationId = applyService.createApplicationForm(applicationFormRequest, recruitmentId,
        user);
    URI location = URI.create("/api/application/" + applicationId);

    return ResponseEntity.created(location).build();
  }

  @Operation(summary = "구인글에 지원한 지원자 확인", description = "고용주가 자신이 올린 구인글에 누가 지원했는지 보여준다.")
  @GetMapping("/{recruitmentId}")
  public ResponseEntity<List<ApplierPerRecruitmentResponse>> searchApplicant(
      @PathVariable Long recruitmentId, @LoginUser User user) {
    List<ApplierPerRecruitmentResponse> applierPerRecruitmentResponses = applyService.searchApplicant(
        recruitmentId, user);
    return ResponseEntity.ok(applierPerRecruitmentResponses);
  }

  @Operation(summary = "지원한 공고 확인", description = "지원자가 자신이 어떤 구인글에 지원했는지 보여준다.")
  @GetMapping("/all")
  public ResponseEntity<List<RecruitmentsOfApplierResponse>> searchMyAppliedRecruitments(
      @LoginUser User user) {
    List<RecruitmentsOfApplierResponse> recruitmentsOfApplierResponses = applyService.searchMyAppliedRecruitments(
        user);
    return ResponseEntity.ok(recruitmentsOfApplierResponses);
  }

  @Operation(summary = "지원자가 지원할 때 필요한 필수 정보 확인", description = "비자, 외국인번호, 이력서 등록 여부 확인")
  @GetMapping()
  public ResponseEntity<MandatoryResponse> checkMandatory(@LoginUser User user) {
    MandatoryResponse mandatoryResponse = applyService.checkMandatory(user);
    return ResponseEntity.ok(mandatoryResponse);
  }
}
