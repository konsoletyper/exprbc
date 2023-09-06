package konsoletyper.exprbc;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.function.Function;

public class ClassGenerator {
  public static final String CLASS_NAME = ClassGenerator.class.getName() + "$Generated";
  private static final String CLASS_INTERNAL_NAME = CLASS_NAME.replace('.', '/');

  public byte[] generate(String expr) {
    var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    ClassVisitor cv = cw;
    if (Boolean.getBoolean("konsoletyper.exprbc.log")) {
      cv = new TraceClassVisitor(cv, new PrintWriter(System.out));
    }

    cv.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, CLASS_INTERNAL_NAME, null,
        Type.getInternalName(Object.class),
        new String[] { Type.getInternalName(Expression.class) });

    generateEmptyConstructor(cv);
    generateWorkerMethod(cv, expr);

    cv.visitEnd();
    return cw.toByteArray();
  }

  private void generateEmptyConstructor(ClassVisitor cv) {
    var mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), null, null);
    mv.visitCode();

    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>",
        Type.getMethodDescriptor(Type.VOID_TYPE), false);
    mv.visitInsn(Opcodes.RETURN);

    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private void generateWorkerMethod(ClassVisitor cv, String expr) {
    var mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "evaluate",
        Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.getType(Function.class)), null, null);
    mv.visitCode();

    var generator = new CodeGenerator(mv);
    var lexer = new Lexer(expr);
    var parser = new Parser(lexer, generator);
    parser.parse();
    mv.visitInsn(Opcodes.DRETURN);

    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }
}
