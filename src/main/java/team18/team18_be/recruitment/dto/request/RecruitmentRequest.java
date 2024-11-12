package team18.team18_be.recruitment.dto.request;

public record RecruitmentRequest(
    String title,
    String companyScale,
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
    Long companyId
) {

}
