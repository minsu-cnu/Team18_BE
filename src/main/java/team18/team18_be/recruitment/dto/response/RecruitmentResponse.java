package team18.team18_be.recruitment.dto.response;

import com.mysql.cj.util.DnsSrv.SrvRecord;

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
    String koreanDetailedDescription,
    String vietnameseDetailedDescription

) {

}
