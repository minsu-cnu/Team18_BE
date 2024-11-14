package team18.team18_be.userInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.config.GCS.FileUtil;
import team18.team18_be.config.GCS.GcsUploader;
import team18.team18_be.userInformation.dto.request.CompanyRequest;
import team18.team18_be.userInformation.dto.request.CompanyResponse;
import team18.team18_be.userInformation.dto.request.VisaRequest;
import team18.team18_be.userInformation.dto.request.VisaResponse;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.entity.ForeignerInformation;
import team18.team18_be.userInformation.repository.CompanyRepository;
import team18.team18_be.userInformation.repository.ForeignerInformationRepository;
import team18.team18_be.userInformation.repository.SignRepository;
import team18.team18_be.userInformation.service.UserInformationService;

public class userInformationServiceTest {

  @InjectMocks
  private UserInformationService userInformationService;
  @Mock
  private ForeignerInformationRepository foreignerInformationRepository;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private SignRepository signRepository;
  @Mock
  private AuthRepository authRepository;
  @Mock
  private GcsUploader gcsUploader;
  @Mock
  private FileUtil fileUtil;

  private User employer;
  private User employee;
  private Company company;
  private ForeignerInformation foreignerInformation;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    employee = new User(1L, "홍길동", "abcd@naver.com", "2");
    employer = new User(2L, "A사장", "employer@naver.com", "1");
    company = new Company(1L, "A", "Food", "Nobrand", 10000L, "aaaa.logoimage.src", employer);
    foreignerInformation = new ForeignerInformation(1L, "123456789", LocalDate.of(2024, 10, 25),
        LocalDate.of(2034, 10, 25), employee);
  }

  @Test
  public void testCreateCompany() {
    //given
    byte[] imageFile = "test data".getBytes();
    CompanyRequest companyRequest = new CompanyRequest("A", "Food", "Nobrand", 10000L);
    MockMultipartFile logoImage = new MockMultipartFile(
        "logoImage",               // 파라미터 이름
        "test.jpg",                // 원본 파일명
        "image/jpeg",              // MIME 타입
        "test data".getBytes()     // 파일 데이터 (바이트 배열)
    );
    when(fileUtil.safelyGetBytes(any(MultipartFile.class))).thenReturn(Optional.of(imageFile));
    when(gcsUploader.upload(any(byte[].class), any(String.class), any(String.class)))
        .thenReturn(Optional.of("aaaa.logoimage.src"));
    when(companyRepository.save(any(Company.class))).thenReturn(company);

    Long result = userInformationService.createCompany(companyRequest, logoImage, employer);

    assertEquals(company.getId(), result);
  }

  @Test
  public void testFindCompany() {
    //given
    List<Company> comapnys = new ArrayList<>();
    comapnys.add(company);
    when(companyRepository.findByUser(employer)).thenReturn(Optional.of(comapnys));
    //when
    List<CompanyResponse> companyResponse = userInformationService.findCompany(employer);
    //then
    assertEquals(company.getId(), companyResponse.get(0).companyId());
  }

  @Test
  public void testFillInVisa() {
    //given
    VisaRequest visaRequest = new VisaRequest("123456789", "2024-10-25");
    when(foreignerInformationRepository.save(any(ForeignerInformation.class))).thenReturn(
        foreignerInformation);
    //when
    Long result = userInformationService.fillInVisa(visaRequest, employee);
    //then
    assertEquals(foreignerInformation.getId(), result);
  }

  @Test
  public void testFindVisa() {
    //given
    Long employeeId = 1L;
    when(authRepository.findById(employeeId)).thenReturn(Optional.of(employee));
    when(foreignerInformationRepository.findByUser(employee)).thenReturn(
        Optional.of(foreignerInformation));
    //when
    VisaResponse visaResponse = userInformationService.findVisa(employeeId);
    //then
    assertEquals(foreignerInformation.getForeignerIdNumber(), visaResponse.foreignerIdNumber());
    assertEquals(foreignerInformation.getVisaGenerateDate().toString(),
        visaResponse.visaGenerateDate());
    assertEquals(foreignerInformation.getVisaExpiryDate().toString(),
        visaResponse.visaExpiryDate());
  }

  @Test
  public void testFillInSign_FileReadError() {
    // given
    MultipartFile imageUrl = new MockMultipartFile("image", "test.jpg", "image/jpeg",
        "test data".getBytes());
    when(fileUtil.safelyGetBytes(any(MultipartFile.class))).thenReturn(Optional.empty());

    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
      userInformationService.fillInSign(imageUrl, employer);
    });
  }

  @Test
  public void testFillInSign_FileUploadError() {
    // given
    MultipartFile imageUrl = new MockMultipartFile("image", "test.jpg", "image/jpeg",
        "test data".getBytes());
    when(fileUtil.safelyGetBytes(any(MultipartFile.class))).thenReturn(
        Optional.of("test data".getBytes()));
    when(gcsUploader.upload(any(byte[].class), any(String.class), any(String.class))).thenReturn(
        Optional.empty());

    // when & then
    assertThrows(NoSuchElementException.class, () -> {
      userInformationService.fillInSign(imageUrl, employer);
    });
  }

}
