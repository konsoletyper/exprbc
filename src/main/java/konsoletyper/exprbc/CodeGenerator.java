package konsoletyper.exprbc;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CodeGenerator implements ParserConsumer {
  private final MethodVisitor mv;
  private final Map<String, Integer> variables = new HashMap<>();
  private int lastVariableIndex = 2;

  public CodeGenerator(MethodVisitor mv) {
    this.mv = mv;
  }

  @Override
  public void number(double value) {
    mv.visitLdcInsn(value);
  }

  @Override
  public void identifier(String id) {
    var index = variables.computeIfAbsent(id, k -> {
      var newIndex = introduceVariable();
      getAndCacheVariable(newIndex, k);
      return newIndex;
    });
    mv.visitVarInsn(Opcodes.DLOAD, index);
  }

  private void getAndCacheVariable(int index, String id) {
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitLdcInsn(id);
    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(Function.class), "apply",
        Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)), true);

    var continueLabel = new Label();
    mv.visitInsn(Opcodes.DUP);
    mv.visitJumpInsn(Opcodes.IFNONNULL, continueLabel);
    reportUndefinedVariable(id);

    mv.visitLabel(continueLabel);
    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Double.class));
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Double.class), "doubleValue",
        Type.getMethodDescriptor(Type.DOUBLE_TYPE), false);
    mv.visitVarInsn(Opcodes.DSTORE, index);
  }

  private void reportUndefinedVariable(String id) {
    mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(ExecutionException.class));
    mv.visitInsn(Opcodes.DUP);
    mv.visitLdcInsn("Undefined variable " + id);
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(ExecutionException.class),
        "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false);
    mv.visitInsn(Opcodes.ATHROW);
  }

  @Override
  public void assignment(String id) {
    var index = variables.computeIfAbsent(id, k -> introduceVariable());
    mv.visitVarInsn(Opcodes.DSTORE, index);
    mv.visitVarInsn(Opcodes.DLOAD, index);
  }

  private int introduceVariable() {
    var result = lastVariableIndex;
    lastVariableIndex += 2;
    return result;
  }

  @Override
  public void add() {
    mv.visitInsn(Opcodes.DADD);
  }

  @Override
  public void subtract() {
    mv.visitInsn(Opcodes.DSUB);
  }

  @Override
  public void multiply() {
    mv.visitInsn(Opcodes.DMUL);
  }

  @Override
  public void divide() {
    mv.visitInsn(Opcodes.DDIV);
  }

  @Override
  public void negate() {
    mv.visitInsn(Opcodes.DNEG);
  }

  @Override
  public void statement() {
    mv.visitInsn(Opcodes.POP2);
  }
}
