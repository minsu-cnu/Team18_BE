package team18.team18_be.recruitment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;

@Entity
public class RecruitmentContent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @NotNull
  private Long resumeContentId;
  @Lob
  @Column(columnDefinition = "TEXT")
  @NotNull
  private String koreanDetailedDescription;
  @Lob
  @Column(columnDefinition = "TEXT")
  @NotNull
  private String vietnameseDetailedDescription;

  public RecruitmentContent(String koreanDetailedDescription,
      String vietnameseDetailedDescription) {
    this.koreanDetailedDescription = koreanDetailedDescription;
    this.vietnameseDetailedDescription = vietnameseDetailedDescription;
  }

  public RecruitmentContent() {
  }

  public Long getResumeContentId() {
    return resumeContentId;
  }

  public String getKoreanDetailedDescription() {
    return koreanDetailedDescription;
  }

  public String getVietnameseDetailedDescription() {
    return vietnameseDetailedDescription;
  }
}
