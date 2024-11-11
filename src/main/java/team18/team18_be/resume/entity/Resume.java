package team18.team18_be.resume.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import team18.team18_be.auth.entity.User;

@Entity
@Getter
@Setter
public class Resume {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long resumeId;
  @NotNull
  private String applicantName;
  @NotNull
  private String address;
  @NotNull
  private String phoneNumber;
  @NotNull
  private String career;
  @NotNull
  private String koreanLanguageLevel;
  @Lob
  @Column(columnDefinition = "TEXT")
  @NotNull
  private String introduction;

  @ManyToOne
  @JoinColumn(name = "userId")
  @NotNull
  private User user;

  public Resume(String applicantName, String address, String phoneNumber, String career,
      String koreanLanguageLevel, String selfIntroduction, User user) {
    this.applicantName = applicantName;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.career = career;
    this.koreanLanguageLevel = koreanLanguageLevel;
    this.introduction = selfIntroduction;
    this.user = user;
  }

  public Resume(Long resumeId, String applicantName, String address, String phoneNumber,
      String career, String koreanLanguageLevel, String selfIntroduction, User user) {
    this.resumeId = resumeId;
    this.applicantName = applicantName;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.career = career;
    this.koreanLanguageLevel = koreanLanguageLevel;
    this.introduction = selfIntroduction;
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Resume resume = (Resume) o;
    return Objects.equals(user.getId(), resume.user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(user);
  }

  public Resume() {
  }

}
