package team18.team18_be.recruitment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import team18.team18_be.userInformation.entity.Company;

@Entity
@Getter
@Setter
public class Recruitment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long recruitmentId;
  @NotNull
  private String koreanTitle;
  @NotNull
  private String vietnameseTitle;
  @NotNull
  private String companySize;
  @NotNull
  private String area;
  @NotNull
  private Long salary;
  @NotNull
  private String workDuration;
  @NotNull
  private String workDays;
  @NotNull
  private String workType;
  @NotNull
  private String workHours;
  @NotNull
  private String requestedCareer;
  @NotNull
  private String majorBusiness;
  @NotNull
  private String eligibilityCriteria;
  @NotNull
  private String preferredConditions;
  @NotNull
  private String employerName;
  @NotNull
  private String companyName;
  @NotNull
  private Boolean hiring;
  @NotNull
  private Date uploadDate;

  @ManyToOne
  @JoinColumn(name = "companyId")
  @NotNull
  private Company company;

  @OneToOne
  @NotNull
  private RecruitmentContent recruitmentContent;

  public Recruitment(String koreanTitle, String vietnameseTitle, String companySize, String area,
      Long salary, String workDuration, String workDays, String workType, String workHours,
      String requestedCareer,
      String majorBusiness, String eligibilityCriteria, String preferredConditions,
      String employerName, String companyName, Company company,
      RecruitmentContent recruitmentContent) {
    this.koreanTitle = koreanTitle;
    this.vietnameseTitle = vietnameseTitle;
    this.companySize = companySize;
    this.area = area;
    this.salary = salary;
    this.workDuration = workDuration;
    this.workDays = workDays;
    this.workType = workType;
    this.workHours = workHours;
    this.requestedCareer = requestedCareer;
    this.majorBusiness = majorBusiness;
    this.eligibilityCriteria = eligibilityCriteria;
    this.preferredConditions = preferredConditions;
    this.employerName = employerName;
    this.companyName = companyName;
    this.company = company;
    this.recruitmentContent = recruitmentContent;
  }


  public Recruitment() {

  }

}
