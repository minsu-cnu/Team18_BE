package team18.team18_be.recruitment.dto.response;

public record RecruitmentResponseForCompany(
    Long recruitmentId,
    String imageUrl,
    String koreanTitle,
    String vietnameseTitle,
    String companyName,
    Long salary,
    String area,
    Boolean hiring
) {

}
