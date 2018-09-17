package exception;

public class SQLFormatException extends Exception {

  public SQLFormatException() {
  }

  public SQLFormatException(String message) {
    super(message);
  }

  public SQLFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
