package team18.team18_be.resume.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team18.team18_be.auth.entity.User;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.resume.dto.request.ResumeRequest;
import team18.team18_be.resume.dto.response.ResumeAndApplyResponse;
import team18.team18_be.resume.dto.response.ResumeResponse;
import team18.team18_be.resume.service.ResumeService;

@Tag(name = "이력서 관련 Controller")
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

  private final ResumeService resumeService;

  public ResumeController(ResumeService resumeService) {
    this.resumeService = resumeService;
  }

  @Operation(summary = "이력서 저장 메서드")
  @PostMapping
  public ResponseEntity<Void> saveResume(
      @RequestBody ResumeRequest resumeRequest,
      @LoginUser User user
  ) {
    resumeService.saveResume(resumeRequest, user);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "구직자 별 이력서 조회 메서드")
  @GetMapping
  public ResponseEntity<ResumeResponse> getResume(
      @LoginUser User user
  ) {
    return ResponseEntity.ok().body(resumeService.findResumeByEmployee(user));
  }

  @Operation(summary = "이력서 id로 이력서 조회 메서드")
  @GetMapping("/{resumeId}/{applyId}")
  public ResponseEntity<ResumeAndApplyResponse> getResumeById(
      @PathVariable Long resumeId,
      @PathVariable Long applyId,
      @LoginUser User user
  ) {

    return ResponseEntity.ok().body(resumeService.findResumeById(resumeId, applyId));
  }

  @Operation(summary = "해당 유저의 이력서 존재 여부 확인")
  @GetMapping("/existence")
  public ResponseEntity<Void> existence(
      @LoginUser User user
  ) {
    resumeService.existence(user);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
