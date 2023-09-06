package konsoletyper.exprbc;

public interface ParserConsumer {
  void number(double value);

  void identifier(String id);

  void assignment(String id);

  void add();

  void subtract();

  void multiply();

  void divide();

  void negate();

  void statement();
}
