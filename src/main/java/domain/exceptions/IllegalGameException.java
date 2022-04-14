package domain.exceptions;

public class IllegalGameException extends RuntimeException {

  public IllegalGameException(String message) {
    super(message);
  }
}
