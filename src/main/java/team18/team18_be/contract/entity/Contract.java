package team18.team18_be.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import team18.team18_be.apply.entity.Apply;

@Entity(name = "contracts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int salary;
  private String workingHours;
  private String dayOff;
  private String annualPaidLeave;
  private String workingPlace;
  private String responsibilities;
  private String rule;
  private String imageFileUrl;
  private String pdfFileUrl;
  private String imageFileUrlV;
  private String pdfFileUrlV;
  @ManyToOne
  @JoinColumn(name = "applyId")
  private Apply apply;

  public String getPdfFileUrl() {
    return this.pdfFileUrl;
  }

  public String getImageFileUrl() {
    return this.imageFileUrl;
  }

  public String getImageFileUrlV() {
    return imageFileUrlV;
  }

  public String getPdfFileUrlV() {
    return pdfFileUrlV;
  }

  public int getSalary() {
    return salary;
  }

  public String getWorkingHours() {
    return workingHours;
  }

  public String getDayOff() {
    return dayOff;
  }

  public String getAnnualPaidLeave() {
    return annualPaidLeave;
  }

  public String getWorkingPlace() {
    return workingPlace;
  }

  public String getResponsibilities() {
    return responsibilities;
  }

  public String getRule() {
    return rule;
  }


  public void updatePdfFileUrl(String pdfFileUrl) {
    this.pdfFileUrl = pdfFileUrl;
  }

  public void updateImageFileUrl(String imageFileUrl) {
    this.imageFileUrl = imageFileUrl;
  }

  public void updatePdfFileUrlV(String pdfFileUrl) {
    this.pdfFileUrlV = pdfFileUrl;
  }

  public void updateImageFileUrlV(String imageFileUrl) {
    this.imageFileUrlV = imageFileUrl;
  }
}
