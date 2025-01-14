package team18.team18_be.userInformation.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team18.team18_be.auth.entity.User;
import team18.team18_be.auth.repository.AuthRepository;
import team18.team18_be.config.GCS.FileUtil;
import team18.team18_be.config.GCS.GcsUploader;
import team18.team18_be.userInformation.dto.request.CompanyRequest;
import team18.team18_be.userInformation.dto.request.CompanyResponse;
import team18.team18_be.userInformation.dto.request.SignResponse;
import team18.team18_be.userInformation.dto.request.VisaRequest;
import team18.team18_be.userInformation.dto.request.VisaResponse;
import team18.team18_be.userInformation.entity.Company;
import team18.team18_be.userInformation.entity.ForeignerInformation;
import team18.team18_be.userInformation.entity.Sign;
import team18.team18_be.userInformation.repository.CompanyRepository;
import team18.team18_be.userInformation.repository.ForeignerInformationRepository;
import team18.team18_be.userInformation.repository.SignRepository;

@Service
public class UserInformationService {

  private final ForeignerInformationRepository foreignerInformationRepository;
  private final CompanyRepository companyRepository;
  private final SignRepository signRepository;
  private final AuthRepository authRepository;
  private final GcsUploader gcsUploader;
  private final FileUtil fileUtil;
  @Value("${company.default-logo-url}")
  private String defaultLogoUrl;

  public UserInformationService(ForeignerInformationRepository foreignerInformationRepository,
      CompanyRepository companyRepository, SignRepository signRepository,
      AuthRepository authRepository, GcsUploader gcsUploader,
      FileUtil fileUtil) {
    this.foreignerInformationRepository = foreignerInformationRepository;
    this.companyRepository = companyRepository;
    this.signRepository = signRepository;
    this.authRepository = authRepository;
    this.gcsUploader = gcsUploader;
    this.fileUtil = fileUtil;
  }


  public Long createCompany(CompanyRequest companyRequest, MultipartFile logoImage, User user) {
    String storedFileName = getStoredFileName(logoImage, user);
    Company company = createCompanyEntity(companyRequest, storedFileName, user);
    return companyRepository.save(company).getId();
  }

  private String getStoredFileName(MultipartFile logoImage, User user) {
    if (logoImage == null) {
      return defaultLogoUrl;
    }
    byte[] imageFile = fileUtil.safelyGetBytes(logoImage)
        .orElseThrow(() -> new IllegalArgumentException("multipart 파일을 읽지 못하였습니다."));

    return gcsUploader.upload(imageFile, "companyLogo",
            user.getId() + "Real" + logoImage.getOriginalFilename())
        .orElseThrow(() -> new NoSuchElementException("파일 업로드에 실패했습니다."));
  }

  private Company createCompanyEntity(CompanyRequest request, String logoUrl, User user) {
    return new Company(
        request.name(),
        request.industryOccupation(),
        request.brand(),
        request.revenuePerYear(),
        logoUrl,
        user
    );
  }

  public List<CompanyResponse> findCompany(User user) {
    return companyRepository.findByUser(user).orElse(Collections.emptyList()).stream()
        .map(this::createCompanyResponse)
        .collect(Collectors.toList());
  }

  private CompanyResponse createCompanyResponse(Company company) {
    CompanyResponse companyResponse = new CompanyResponse(company.getId(), company.getName(),
        company.getIndustryOccupation(), company.getBrand(), company.getRevenuePerYear(),
        company.getLogoImage());
    return companyResponse;
  }

  public Long fillInVisa(VisaRequest visaRequest, User user) {
    LocalDate visaGenerateDate = LocalDate.parse(visaRequest.visaGenerateDate());
    LocalDate visaExpiryDate = visaGenerateDate.plusYears(10);
    ForeignerInformation newForeignerInformation = new ForeignerInformation(user.getId(),
        visaRequest.foreignerIdNumber(),
        visaGenerateDate, visaExpiryDate, user);
    ForeignerInformation savedForeignerInformation = foreignerInformationRepository.save(
        newForeignerInformation);
    return savedForeignerInformation.getId();
  }

  public VisaResponse findVisa(Long userId) {
    User user = authRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당 유저가 없습니다."));
    ForeignerInformation foreignerInformation = foreignerInformationRepository.findByUser(user)
        .orElseThrow(() -> new NoSuchElementException("해당 외국인 정보가 없습니다"));
    String visaGenerateDate = foreignerInformation.getVisaGenerateDate().toString();
    String visaExpiryDate = foreignerInformation.getVisaExpiryDate().toString();
    VisaResponse visaResponse = new VisaResponse(foreignerInformation.getForeignerIdNumber(),
        visaGenerateDate, visaExpiryDate);
    return visaResponse;
  }

  public Long fillInSign(MultipartFile imageUrl, User user) {
    byte[] imageFile = fileUtil.safelyGetBytes(imageUrl)
        .orElseThrow(() -> new IllegalArgumentException("multipart 파일을 읽지 못하였습니다."));
    String storedFileName = gcsUploader.upload(imageFile, "Sign",
            user.getId().toString() + RandomStringUtils.random(5) + imageUrl.getOriginalFilename())
        .orElseThrow(() -> new NoSuchElementException("파일 업로드에 실패했습니다."));
    Sign newSign = new Sign(storedFileName, user);
    Sign savedSign = signRepository.save(newSign);
    return savedSign.getId();
  }

  public SignResponse findSign(User user) {
    Sign sign = signRepository.findByUser(user)
        .orElseThrow(() -> new NoSuchElementException("해당 사인이 없습니다."));
    SignResponse signResponse = new SignResponse(sign.getImageUrl());
    return signResponse;
  }

}
