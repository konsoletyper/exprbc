package konsoletyper.exprbc.test;

import konsoletyper.exprbc.Compiler;
import konsoletyper.exprbc.ExecutionException;
import konsoletyper.exprbc.ParseException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CompilerTest {
  private final Compiler compiler = new Compiler(CompilerTest.class.getClassLoader());
  private final Map<String, Double> inputs = new HashMap<>();

  @Test
  public void number() {
    assertEquals(23.0, evaluate("23"), 0.001);
  }

  @Test
  public void identifier() {
    var expr = compiler.compile("foo");

    try {
      expr.evaluate(inputs::get);
      fail();
    } catch (ExecutionException e) {
      // exception was expected
    }

    inputs.put("foo", 23.0);
    assertEquals(23.0, expr.evaluate(inputs::get), 0.001);
  }

  @Test
  public void operations() {
    assertEquals(5.0, evaluate("2 + 3"), 0.001);
    assertEquals(-1.0, evaluate("2 - 3"), 0.001);
    assertEquals(6.0, evaluate("2 * 3"), 0.001);
    assertEquals(1.5, evaluate("3 / 2"), 0.001);
    assertEquals(-3.0, evaluate("-3"), 0.001);
  }

  @Test
  public void priority() {
    assertEquals(14.0, evaluate("2 + 3 * 4"), 0.001);
    assertEquals(20.0, evaluate("(2 + 3) * 4"), 0.001);
    assertEquals(10.0, evaluate("2 * 3 + 4"), 0.001);
    assertEquals(14.0, evaluate("2 * (3 + 4)"), 0.001);
  }

  @Test
  public void statements() {
    assertEquals(9.0, evaluate("a = 2; b = 3; c = a + b; b = 4.0; b + c;"));
  }

  @Test
  public void error() {
    try {
      compiler.compile("!");
      fail();
    } catch (ParseException e) {
      assertEquals(0, e.position);
    }

    try {
      compiler.compile("a b");
      fail();
    } catch (ParseException e) {
      assertEquals(2, e.position);
    }

    try {
      compiler.compile("(a + b");
      fail();
    } catch (ParseException e) {
      assertEquals(6, e.position);
    }

    try {
      compiler.compile("a + b)");
      fail();
    } catch (ParseException e) {
      assertEquals(5, e.position);
    }
  }

  private double evaluate(String str) {
    return compiler.compile(str).evaluate(inputs::get);
  }
}
