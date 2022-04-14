package domain.exceptions;

public class DuplicatedGameKeyException extends RuntimeException {

  public DuplicatedGameKeyException(String message) {
    super(message);
  }
}
