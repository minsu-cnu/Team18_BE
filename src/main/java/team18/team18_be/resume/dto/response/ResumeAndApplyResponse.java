package team18.team18_be.resume.dto.response;

public record ResumeAndApplyResponse(
    Long resumeId,
    String applicantName,
    String address,
    String phoneNumber,
    String career,
    String korean,
    String selfIntroduction,
    String motivation
) {

}
