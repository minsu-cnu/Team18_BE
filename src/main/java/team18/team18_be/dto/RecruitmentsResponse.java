package team18.team18_be.dto;

public record RecruitmentsResponse(
        String title,
        String companySize,
        String area,
        String requestedCareer,
        String detailedDescription,
        String majorBusiness,
        String eligibilityCriteria,
        String preferredConditions,
        String employerName
) {
}
