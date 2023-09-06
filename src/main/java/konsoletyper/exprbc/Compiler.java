package konsoletyper.exprbc;

import java.lang.reflect.InvocationTargetException;

public class Compiler {
  private final ClassLoader classLoader;

  public Compiler(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public Expression compile(String expr) {
    var bytecode = new ClassGenerator().generate(expr);
    var loader = new ProvidedBytecodeClassLoader(classLoader, bytecode);
    Class<?> cls;
    try {
      cls = Class.forName(ClassGenerator.CLASS_NAME, false, loader);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      return (Expression) cls.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
