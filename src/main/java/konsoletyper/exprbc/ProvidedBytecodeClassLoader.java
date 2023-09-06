package konsoletyper.exprbc;

public class ProvidedBytecodeClassLoader extends ClassLoader {
  private final byte[] bytecode;

  public ProvidedBytecodeClassLoader(ClassLoader parent, byte[] bytecode) {
    super(parent);
    this.bytecode = bytecode;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (!name.equals(ClassGenerator.CLASS_NAME)) {
      return super.findClass(name);
    }
    return defineClass(ClassGenerator.CLASS_NAME, bytecode, 0, bytecode.length);
  }
}
