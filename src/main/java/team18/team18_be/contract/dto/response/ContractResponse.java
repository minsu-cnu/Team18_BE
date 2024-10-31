package team18.team18_be.contract.dto.response;

public record ContractResponse(
    int salary,
    String workingHours,
    String dayOff,
    String annualPaidLeave,
    String workingPlace,
    String responsibilities,
    String rule
) {

}
