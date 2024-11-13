package team18.team18_be.exception;

public class ContractAlreadyExistsException extends RuntimeException {

  public ContractAlreadyExistsException() {
  }

  public ContractAlreadyExistsException(String message) {
    super(message);
  }

}
