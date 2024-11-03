package team18.team18_be.recruitment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
  private String koreanTitle;
  private String vietnameseTitle;
  private String companySize;
  private String area;
  private String salary;
  private String workDuration;
  private String workDays;
  private String workType;
  private String workHours;
  private String requestedCareer;
  private String majorBusiness;
  private String eligibilityCriteria;
  private String preferredConditions;
  private String employerName;
  private String companyName;
  private Boolean hiring;
  private Date uploadDate;

  @ManyToOne
  @JoinColumn(name = "companyId")
  private Company company;
  @OneToOne
  private RecruitmentContent recruitmentContent;

  public Recruitment(String koreanTitle, String vietnameseTitle, String companySize, String area,
      String salary, String workDuration, String workDays, String workType, String workHours,
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
