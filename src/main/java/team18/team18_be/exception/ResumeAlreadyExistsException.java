package team18.team18_be.exception;

public class ResumeAlreadyExistsException extends RuntimeException{
  public ResumeAlreadyExistsException() {
  }

  public ResumeAlreadyExistsException(String message) {
    super(message);
  }
}
