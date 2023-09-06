package konsoletyper.exprbc;

public class ParseException extends RuntimeException {
  public final int position;
  public final String error;

  public ParseException(String error, int position) {
    super("Parse error at " + position + ": " + error);
    this.position = position;
    this.error = error;
  }
}
