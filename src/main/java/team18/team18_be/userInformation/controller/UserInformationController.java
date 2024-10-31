package team18.team18_be.userInformation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.service.AuthService;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.userInformation.dto.request.CompanyRequest;
import team18.team18_be.userInformation.dto.request.CompanyResponse;
import team18.team18_be.userInformation.dto.request.SignResponse;
import team18.team18_be.userInformation.dto.request.VisaRequest;
import team18.team18_be.userInformation.dto.request.VisaResponse;
import team18.team18_be.userInformation.entity.Sign;
import team18.team18_be.userInformation.service.UserInformationService;

@RestController
@RequestMapping("/api")
@Tag(name = "유저 정보 API", description = "비자 정보, 회사 정보, 사인 정보")
public class UserInformationController {

  private final UserInformationService userInformationService;

  public UserInformationController(UserInformationService userInformationService) {
    this.userInformationService = userInformationService;
  }

  @Operation(summary = "사인등록", description = "image파일을 받아 gcs에 해당 이미지 저장")
  @PostMapping(value = "/sign")
  public ResponseEntity<Void> fillInSign(@RequestParam MultipartFile imageUrl,
      @LoginUser User user) {
    userInformationService.fillInSign(imageUrl, user);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "사인 가져오기", description = "gcs에 저장된 사인 이미지의 주소 반환")
  @GetMapping(value = "/sign")
  public ResponseEntity<SignResponse> findSign(@LoginUser User user) {
    SignResponse signResponse = userInformationService.findSign(user);
    return ResponseEntity.ok(signResponse);
  }

  @Operation(summary = "회사등록")
  @PostMapping("/company")
  public ResponseEntity<Void> createCompany(@RequestPart CompanyRequest companyRequest,
      @RequestPart MultipartFile logoImage, @LoginUser User user) {
    userInformationService.createCompany(companyRequest, logoImage, user);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "회사 정보 가져오기", description = "로그인된 user의 회사 정보 가져오기")
  @GetMapping("/company")
  public ResponseEntity<CompanyResponse> findCompany(@LoginUser User user) {
    CompanyResponse companyResponse = userInformationService.findCompany(user);
    return ResponseEntity.ok(companyResponse);
  }


  @Operation(summary = "비자 등록", description = "외국인 번호와 비자 생성일 등록")
  @PutMapping("/visa")
  public ResponseEntity<Void> fillInVisa(@RequestBody VisaRequest visaRequest,
      @LoginUser User user) {
    userInformationService.fillInVisa(visaRequest, user);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "비자 정보 가져오기")
  @GetMapping("/visa")
  public ResponseEntity<VisaResponse> findVisa(@RequestParam("userId") Long userId) {
    VisaResponse visaResponse = userInformationService.findVisa(userId);
    return ResponseEntity.ok(visaResponse);
  }
}
