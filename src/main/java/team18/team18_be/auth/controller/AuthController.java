package team18.team18_be.auth.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team18.team18_be.auth.dto.request.CodeRequest;
import team18.team18_be.auth.dto.request.UserTypeRequest;
import team18.team18_be.auth.dto.response.LoginResponse;
import team18.team18_be.auth.dto.response.OAuthJwtResponse;
import team18.team18_be.auth.dto.response.UserTypeResponse;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.service.AuthService;
import team18.team18_be.config.resolver.LoginUser;
import team18.team18_be.exception.ExceptionResponse;

@Tag(name = "인증/인가", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api")
public class AuthController {

  private final AuthService authService;
  @Value("${oauth.google.token-uri}")
  private String GOOGLE_TOKEN_URI;
  @Value("${oauth.google.user-info-uri}")
  private String GOOGLE_USER_INFO_URI;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTypeResponse.class))),
      @ApiResponse(responseCode = "401", description = "구글 토큰 발급 관련 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "401", description = "구글 유저 정보 조회 관련 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "404", description = "회원 정보 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
      @ApiResponse(responseCode = "500", description = "서버 내부 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
  })
  @PostMapping("/oauth")
  public ResponseEntity<UserTypeResponse> login(@RequestBody CodeRequest codeRequest,
      HttpServletRequest request) {
    String referer = request.getHeader("Referer");

    OAuthJwtResponse oAuthJwtResponse = authService.getOAuthToken(codeRequest,
        GOOGLE_TOKEN_URI, referer);
    LoginResponse loginResponse = authService.registerOAuth(oAuthJwtResponse,
        GOOGLE_USER_INFO_URI);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(loginResponse.accessToken());

    UserTypeResponse userTypeResponse = new UserTypeResponse(loginResponse.type(),
        loginResponse.profileImage(), loginResponse.name());

    return ResponseEntity.ok().headers(headers).body(userTypeResponse);
  }

  @ApiResponse(responseCode = "204", description = "유저 타입 등록 성공")
  @PostMapping("/register")
  public ResponseEntity<Void> registerUserType(@Valid @RequestBody UserTypeRequest userTypeRequest,
      @LoginUser User user) {
    authService.registerUserType(userTypeRequest, user);
    return ResponseEntity.noContent().build();
  }
}
