package konsoletyper.exprbc;

public class Lexer {
  private final CharSequence inputString;
  private int position;
  private Token token;
  private int tokenPosition;
  private double number;
  private String identifier;

  public Lexer(CharSequence inputString) {
    this.inputString = inputString;
  }

  public void next() {
    if (token == Token.EOF) {
      return;
    }
    skipWhitespace();
    identifier = null;
    number = 0;
    tokenPosition = position;
    if (position == inputString.length()) {
      token = Token.EOF;
      return;
    }

    var c = inputString.charAt(position);
    switch (c) {
      case '+':
        simpleToken(Token.PLUS);
        break;
      case '-':
        simpleToken(Token.MINUS);
        break;
      case '*':
        simpleToken(Token.STAR);
        break;
      case '/':
        simpleToken(Token.SLASH);
        break;
      case '(':
        simpleToken(Token.LEFT_BRACE);
        break;
      case ')':
        simpleToken(Token.RIGHT_BRACE);
        break;
      case '=':
        simpleToken(Token.ASSIGNMENT);
        break;
      case ';':
        simpleToken(Token.SEMICOLON);
        break;
      default:
        if (isIdentifierStart(c)) {
          parseIdentifier();
        } else if (isDigit(c)) {
          parseNumber();
        } else {
          error("Unexpected character");
        }
        break;
    }
  }

  private void simpleToken(Token token) {
    this.token = token;
    ++position;
  }

  private void skipWhitespace() {
    while (position < inputString.length() && Character.isWhitespace(inputString.charAt(position))) {
      ++position;
    }
  }

  private void parseIdentifier() {
    token = Token.IDENTIFIER;
    tokenPosition = position;
    while (position < inputString.length() && isIdentifierPart(inputString.charAt(position))) {
      ++position;
    }
    identifier = inputString.subSequence(tokenPosition, position).toString();
  }

  private void parseNumber() {
    token = Token.NUMBER;
    while (position < inputString.length() && isDigit(inputString.charAt(position))) {
      ++position;
    }
    if (position < inputString.length() && inputString.charAt(position) == '.') {
      ++position;
      if (position >= inputString.length() || !isDigit(inputString.charAt(position))) {
        error("Invalid number literal");
      }
      while (position < inputString.length() && isDigit(inputString.charAt(position))) {
        ++position;
      }
    }
    try {
      number = Double.parseDouble(inputString.subSequence(tokenPosition, position).toString());
    } catch (NumberFormatException e) {
      error("Invalid number literal");
    }
  }

  private static boolean isIdentifierStart(char c) {
    switch (c) {
      case '$':
      case '_':
        return true;
      default:
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }
  }

  private static boolean isIdentifierPart(char c) {
    return isIdentifierStart(c) || isDigit(c);
  }

  private static boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void error(String error) {
    throw new ParseException(error, position);
  }

  public int getTokenPosition() {
    return tokenPosition;
  }

  public Token getToken() {
    return token;
  }

  public double getNumber() {
    return number;
  }

  public String getIdentifier() {
    return identifier;
  }
}
