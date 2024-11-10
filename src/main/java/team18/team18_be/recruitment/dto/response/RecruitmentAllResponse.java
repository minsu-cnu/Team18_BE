package team18.team18_be.recruitment.dto.response;

import java.util.List;

public record RecruitmentAllResponse(
    List<RecruitmentSummationResponse> content,
    PageDto pageable
) {

}
