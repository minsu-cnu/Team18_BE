package team18.team18_be.apply.ApplyStatusEnum;

public enum ApplyStatus {
  REVIEWING_APPLICATION("지원서 검토중"),
  SIGNING_EMPLOYMENT_CONTRACT("근로계약서 서명하기"),
  HIRING_CLOSED("채용마감"),
  HIRED("채용완료");

  private final String koreanName;


  ApplyStatus(String koreanName) {
    this.koreanName = koreanName;
  }

  public String getKoreanName() {
    return koreanName;
  }
}
