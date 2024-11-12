package team18.team18_be.recruitment.dto.response;

public record RecruitmentResponse(
    String koreanTitle,
    String vietnameseTitle,
    String companyScale,
    String imageUrl,
    String area,
    Long salary,
    String workDuration,
    String workDays,
    String workType,
    String workHours,
    String requestedCareer,
    String majorBusiness,
    String eligibilityCriteria,
    String preferredConditions,
    String employerName,
    String companyName,
    String koreanDetailedDescription,
    String vietnameseDetailedDescription

) {

}
