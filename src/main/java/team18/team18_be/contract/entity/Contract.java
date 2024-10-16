package team18.team18_be.contract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import team18.team18_be.apply.entity.ApplicationForm;
import team18.team18_be.auth.entity.User;

@Entity(name = "contracts")
public class Contract {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int salary;
  private String contractPeriod;
  private String dayOff;
  private String annualPaidLeave;
  private String workingPlace;
  private String responsibilities;
  private String imageFileUrl;
  private String pdfFileUrl;
  private String rule;
  @ManyToOne
  @JoinColumn(name = "applicationId")
  private ApplicationForm applicationForm;
}
