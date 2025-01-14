package team18.team18_be.apply.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table
public class ApplicationForm {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Size(max = 10, message = "최대 10글자까지 입력 가능합니다.")
  @NotNull
  private String name;
  @NotNull
  private String address;
  @Pattern(regexp = "^010\\d{8}$", message = "-없이 입력해주세요. ex) 01012345678")
  @NotNull
  private String phoneNumber;
  @Size(max = 500, message = "500글자 내로 입력해주세요.")
  @NotNull
  private String motivation;

  @OneToOne
  @JoinColumn(name = "apply_id")
  @NotNull
  private Apply apply;

  public ApplicationForm() {
  }

  public ApplicationForm(String name, String address, String phoneNumber, String motivation,
      Apply apply) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.motivation = motivation;
    this.apply = apply;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getMotivation() {
    return motivation;
  }

  public Apply getApply() {
    return apply;
  }
}
