package konsoletyper.exprbc;

public class Parser {
  private final Lexer lexer;
  private final ParserConsumer consumer;

  public Parser(Lexer lexer, ParserConsumer consumer) {
    this.lexer = lexer;
    this.consumer = consumer;
  }

  public void parse() {
    lexer.next();
    parseSum();
    while (lexer.getToken() == Token.SEMICOLON) {
      skipSemicolons();
      if (lexer.getToken() == Token.EOF) {
        break;
      }
      consumer.statement();
      parseSum();
    }
    if (lexer.getToken() != Token.EOF) {
      error("End of input expected");
    }
  }

  private void skipSemicolons() {
    while (lexer.getToken() == Token.SEMICOLON) {
      lexer.next();
    }
  }

  private void parseSum() {
    parseProd();
    while (lexer.getToken() == Token.PLUS || lexer.getToken() == Token.MINUS) {
      var token = lexer.getToken();
      lexer.next();
      parseProd();
      if (token == Token.PLUS) {
        consumer.add();
      } else {
        consumer.subtract();
      }
    }
  }

  private void parseProd() {
    parsePrime();
    while (lexer.getToken() == Token.STAR || lexer.getToken() == Token.SLASH) {
      var token = lexer.getToken();
      lexer.next();
      parsePrime();
      if (token == Token.STAR) {
        consumer.multiply();
      } else {
        consumer.divide();
      }
    }
  }

  private void parsePrime() {
    switch (lexer.getToken()) {
      case NUMBER:
        consumer.number(lexer.getNumber());
        lexer.next();
        break;
      case IDENTIFIER:
        parseIdentifierOrAssignment();
        break;
      case MINUS:
        lexer.next();
        parsePrime();
        consumer.negate();
        break;
      case LEFT_BRACE:
        lexer.next();
        parseParenthesized();
        break;
      default:
        error("Unexpected token");
        break;
    }
  }

  private void parseIdentifierOrAssignment() {
    var id = lexer.getIdentifier();
    lexer.next();
    if (lexer.getToken() == Token.ASSIGNMENT) {
      lexer.next();
      parseSum();
      consumer.assignment(id);
    } else {
      consumer.identifier(id);
    }
  }

  private void parseParenthesized() {
    parseSum();
    if (lexer.getToken() != Token.RIGHT_BRACE) {
      error("Closing brace expected");
    }
    lexer.next();
  }

  private void error(String error) {
    throw new ParseException(error, lexer.getTokenPosition());
  }
}
