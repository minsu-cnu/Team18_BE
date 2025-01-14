package team18.team18_be.auth.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import team18.team18_be.auth.dto.request.CodeRequest;
import team18.team18_be.auth.dto.request.UserTypeRequest;
import team18.team18_be.auth.dto.response.LoginResponse;
import team18.team18_be.auth.dto.response.OAuthJwtResponse;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.entity.UserType;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.config.property.GoogleProperty;
import team18.team18_be.exception.ErrorMessage;
import team18.team18_be.exception.OAuthLoginFailedException;

@Service
public class AuthService {

  public static final String USER_ID = "userId";
  public static final String AUTHORIZATION_CODE = "code";
  public static final String CLIENT_ID = "client_id";
  public static final String CLIENT_SECRET = "client_secret";
  public static final String REDIRECT_URI = "redirect_uri";
  public static final String GRANT_TYPE = "grant_type";
  public static final String EMAIL = "email";
  public static final String NAME = "name";
  public static final String PICTURE = "picture";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";
  public static final String ACCESS_TOKEN = "access_token";
  public static final String LOCALHOST = "localhost";
  private final AuthRepository authRepository;
  private final GoogleProperty googleProperty;
  private final RestClient restClient = RestClient.builder().build();

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  public AuthService(AuthRepository authRepository, GoogleProperty googleProperty) {
    this.authRepository = authRepository;
    this.googleProperty = googleProperty;
  }

  public OAuthJwtResponse getOAuthToken(CodeRequest codeRequest, String externalApiUri,
      String referer) {
    validateReferer(referer);
    LinkedMultiValueMap<String, String> requestBody = getRequestBody(codeRequest, referer);

    ResponseEntity<String> response = restClient.post()
        .uri(URI.create(externalApiUri))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(requestBody)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, ((req, res) -> {
          throw new OAuthLoginFailedException(
              ErrorMessage.GOOGLE_OAUTH_TOKEN_ERROR_MESSAGE.getErrorMessage());
        }))
        .toEntity(String.class);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(response.getBody());
      String accessToken = rootNode.path(ACCESS_TOKEN).asText();
      return new OAuthJwtResponse(accessToken);
    } catch (Exception e) {
      throw new InternalException(ErrorMessage.OBJECT_MAPPER_ERROR_MESSAGE.getErrorMessage());
    }
  }

  public LoginResponse registerOAuth(OAuthJwtResponse oAuthJwtResponse, String externalApiUri) {
    ResponseEntity<String> response = restClient.get()
        .uri(URI.create(externalApiUri))
        .header(AUTHORIZATION, BEARER + oAuthJwtResponse.accessToken())
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, ((req, res) -> {
          throw new OAuthLoginFailedException(
              ErrorMessage.GOOGLE_OAUTH_USER_INFO_ERROR_MESSAGE.getErrorMessage());
        }))
        .toEntity(String.class);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(response.getBody());
      String userEmail = rootNode.path(EMAIL).asText();

      if (!authRepository.existsByEmail(userEmail)) {
        String userName = rootNode.path(NAME).asText();
        authRepository.save(new User(userName, userEmail, UserType.FIRST.getUserType()));
      }

      String profileImage = rootNode.path(PICTURE).asText();
      return getLoginResponse(userEmail, profileImage);
    } catch (Exception e) {
      throw new InternalException(ErrorMessage.OBJECT_MAPPER_ERROR_MESSAGE.getErrorMessage());
    }
  }

  public void registerUserType(UserTypeRequest userTypeRequest, User user) {
    authRepository.save(user.updateUserType(userTypeRequest.type()));
  }

  private LinkedMultiValueMap<String, String> getRequestBody(CodeRequest codeRequest,
      String referer) {
    LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(AUTHORIZATION_CODE, codeRequest.code());
    requestBody.add(CLIENT_ID, googleProperty.clientId());
    requestBody.add(CLIENT_SECRET, googleProperty.clientSecret());
    requestBody.add(GRANT_TYPE, googleProperty.grantType());

    if (referer.contains(LOCALHOST)) {
      requestBody.add(REDIRECT_URI, googleProperty.redirectUriLocal());
    } else {
      requestBody.add(REDIRECT_URI, googleProperty.redirectUriProd());
    }

    return requestBody;
  }

  private LoginResponse getLoginResponse(String userEmail,
      String profileImage) {
    User user = authRepository.findByEmail(userEmail)
        .orElseThrow(
            () -> new NoSuchElementException(ErrorMessage.NOT_FOUND_USER.getErrorMessage()));
    String userType = user.getType();
    String accessToken = getAccessToken(user);
    return new LoginResponse(accessToken, userType, profileImage, user.getName());
  }

  private String getAccessToken(User user) {
    byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    SecretKey key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.builder()
        .claim(USER_ID, user.getId())
        .signWith(key)
        .compact();
  }

  private void validateReferer(String referer) {
    if (referer == null) {
      throw new IllegalCallerException(ErrorMessage.NOT_FOUND_REFERER_IN_HEADER.getErrorMessage());
    }
  }
}
