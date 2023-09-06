package konsoletyper.exprbc;

import java.util.function.Function;

public interface Expression {
  double evaluate(Function<String, Double> inputs);
}
