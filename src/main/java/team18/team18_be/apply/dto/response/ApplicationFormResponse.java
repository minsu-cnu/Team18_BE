package team18.team18_be.apply.dto.response;

public record ApplicationFormResponse(
    String name,
    String address,
    String phoneNumber,

    String motivation
) {

}
