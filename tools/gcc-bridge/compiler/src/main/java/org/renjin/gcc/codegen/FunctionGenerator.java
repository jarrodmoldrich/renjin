package org.renjin.gcc.codegen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.renjin.gcc.GimpleCompiler;
import org.renjin.gcc.InternalCompilerException;
import org.renjin.gcc.TreeLogger;
import org.renjin.gcc.codegen.call.CallGenerator;
import org.renjin.gcc.codegen.call.InvocationStrategy;
import org.renjin.gcc.codegen.condition.ConditionGenerator;
import org.renjin.gcc.codegen.expr.*;
import org.renjin.gcc.codegen.type.ParamStrategy;
import org.renjin.gcc.codegen.type.ReturnStrategy;
import org.renjin.gcc.codegen.type.TypeOracle;
import org.renjin.gcc.codegen.type.TypeStrategy;
import org.renjin.gcc.gimple.*;
import org.renjin.gcc.gimple.expr.GimpleFunctionRef;
import org.renjin.gcc.gimple.statement.*;
import org.renjin.gcc.gimple.type.GimpleVoidType;
import org.renjin.gcc.peephole.PeepholeOptimizer;
import org.renjin.gcc.symbols.LocalVariableTable;
import org.renjin.gcc.symbols.UndefinedCollector;
import org.renjin.gcc.symbols.UnitSymbolTable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates the bytecode for a {@link GimpleFunction}
 */
public class FunctionGenerator implements InvocationStrategy {

  private String className;
  private GimpleFunction function;
  private Map<GimpleParameter, ParamStrategy> params = Maps.newHashMap();
  private ReturnStrategy returnStrategy;
  
  private Labels labels = new Labels();
  private TypeOracle typeOracle;
  private ExprFactory exprFactory;
  private LocalVariableTable symbolTable;
  
  private Label beginLabel = new Label();
  private Label endLabel = new Label();
  
  private MethodGenerator mv;

  public FunctionGenerator(String className, GimpleFunction function, TypeOracle typeOracle, UnitSymbolTable symbolTable) {
    this.className = className;
    this.function = function;
    this.typeOracle = typeOracle;
    this.params = this.typeOracle.forParameters(function.getParameters());
    this.returnStrategy = this.typeOracle.returnStrategyFor(function.getReturnType());
    this.symbolTable = new LocalVariableTable(symbolTable);
    this.exprFactory = new ExprFactory(typeOracle, this.symbolTable);
  }

  public String getMangledName() {
    return function.getMangledName();
  }
  
  public List<String> getMangledNames() {
    List<String> names = Lists.newArrayList();
    names.add(function.getMangledName());
    names.addAll(function.getAliases());
    return names;
  }

  public GimpleFunction getFunction() {
    return function;
  }

  public void emit(TreeLogger parentLogger, ClassVisitor cw) {

    TreeLogger logger = parentLogger.enter("Generating bytecode for " + 
        function.getName() + " [" + function.getMangledName() + "]");
    logger.debug("Aliases: " + function.getAliases());
    logger.debug("Gimple:", function);

    if(GimpleCompiler.TRACE) {
      System.out.println(function);
    }
    
    MethodNode methodNode = new MethodNode(ACC_PUBLIC | ACC_STATIC, 
        function.getMangledName(), 
        getFunctionDescriptor(), null, null);

    mv = new MethodGenerator(methodNode);
    mv.visitCode();
    mv.visitLabel(beginLabel);
    
    emitParamInitialization();
    scheduleLocalVariables();
    
    emitLocalVarInitialization();

    for (GimpleBasicBlock basicBlock : function.getBasicBlocks()) {
      emitBasicBlock(basicBlock);
    }
    
    // Verify that GCC is not letting us fall through with out a return statement
    GimpleBasicBlock lastBlock = function.getLastBasicBlock();
    if(lastBlock.fallsThrough()) {
      SimpleExpr defaultReturnValue = returnStrategy.getDefaultReturnValue();
      defaultReturnValue.load(mv);
      mv.areturn(defaultReturnValue.getType());
    }
    
    mv.visitLabel(endLabel);

    // Javac does not like our variable table
    // https://bugs.openjdk.java.net/browse/JDK-8132697
    // Bug is somewhere in ClassReader.java
    // http://hg.openjdk.java.net/jdk8u/jdk8u/langtools/file/78f0aa619915/src/share/classes/com/sun/tools/javac/jvm/ClassReader.java
      
    mv.getLocalVarAllocator().emitDebugging(mv, beginLabel, endLabel);

    mv.visitMaxs(1, 1);
    mv.visitEnd();

    // Reduce the size of the bytecode by applying simple optimizations
    PeepholeOptimizer.INSTANCE.optimize(methodNode);

    logger.debug("Generated bytecode: ", toString(methodNode));

    try {
      methodNode.accept(cw);

    } catch (Exception e) {
      
      // Include the generated bytecode as part of the stack trace so we can
      // see what went wrong
      throw new InternalCompilerException("Error in generated byte code for " + function.getName() + "\n" + 
          "Offending bytecode:\n" + toString(methodNode), e);
    }

  }

  private String toString(MethodNode methodNode) {
    try {
      Textifier p = new Textifier();
      methodNode.accept(new TraceMethodVisitor(p));
      StringWriter sw = new StringWriter();
      try (PrintWriter pw = new PrintWriter(sw)) {
        p.print(pw);
      }
      return sw.toString();
    } catch (Exception e) {
      return "<Exception generating bytecode: " + e.getClass().getName() + ": " + e.getMessage() + ">";
    }
  }

  private void emitParamInitialization() {
    // first we need to map the parameters to their indexes in the local variable table
    int numParameters = function.getParameters().size();
    List<List<SimpleLValue>> paramIndexes = new ArrayList<>();

    for (int i = 0; i < numParameters; i++) {
      List<SimpleLValue> paramVars = new ArrayList<>();
      GimpleParameter param = function.getParameters().get(i);
      ParamStrategy paramStrategy = params.get(param);
      List<Type> parameterTypes = paramStrategy.getParameterTypes();
      if(parameterTypes.size() == 1) {
        paramVars.add(mv.getLocalVarAllocator().reserve(param.getName(), parameterTypes.get(0)));
      } else {
        for (int typeIndex = 0; typeIndex < parameterTypes.size(); typeIndex++) {
          paramVars.add(mv.getLocalVarAllocator().reserve(param.getName() + "$" + typeIndex, parameterTypes.get(typeIndex)));
        }
      }
      paramIndexes.add(paramVars);
    }

    // Now do any required initialization
    for (int i = 0; i < numParameters; i++) {
      GimpleParameter param = function.getParameters().get(i);
      ParamStrategy generator = params.get(param);
      Expr expr = generator.emitInitialization(mv, param, paramIndexes.get(i), mv.getLocalVarAllocator());
      symbolTable.addVariable(param.getId(), expr);
    }
  }

  private void emitLocalVarInitialization() {
    
    mv.getLocalVarAllocator().initializeVariables(mv);
    
    for (GimpleVarDecl decl : function.getVariableDeclarations()) {
      LValue<Expr> lhs = (LValue<Expr>) symbolTable.getVariable(decl);
      if(decl.getValue() != null) {
        lhs.store(mv, exprFactory.findGenerator(decl.getValue()));
      }
    }
  }

  /*
   * Assign symbols to local variable slots.
   */
  private void scheduleLocalVariables() {

    // Dumb scheduling: give every local variable it's own slot
    for (GimpleVarDecl varDecl : function.getVariableDeclarations()) {
      
      try {
        Expr generator;
        TypeStrategy factory = typeOracle.forType(varDecl.getType());
        generator = factory.variable(varDecl, mv.getLocalVarAllocator());

        symbolTable.addVariable(varDecl.getId(), generator);
      } catch (Exception e) {
        throw new InternalCompilerException("Exception generating local variable " + varDecl, e);
      }
    }
  }

  private void emitBasicBlock(GimpleBasicBlock basicBlock) {
    mv.visitLabel(labels.of(basicBlock));

    for (GimpleStatement ins : basicBlock.getStatements()) {
      Label insLabel = new Label();
      mv.visitLabel(insLabel);
      
      try {
        if (ins instanceof GimpleAssignment) {
          emitAssignment((GimpleAssignment) ins);
        } else if (ins instanceof GimpleReturn) {
          emitReturn((GimpleReturn) ins);
        } else if (ins instanceof GimpleGoto) {
          emitGoto((GimpleGoto) ins);
        } else if (ins instanceof GimpleConditional) {
          emitConditional((GimpleConditional) ins);
        } else if (ins instanceof GimpleCall) {
          emitCall((GimpleCall) ins);
        } else if (ins instanceof GimpleSwitch) {
          emitSwitch((GimpleSwitch) ins);
        } else {
          throw new UnsupportedOperationException("ins: " + ins);
        }
      } catch (Exception e) {
        throw new InternalCompilerException("Exception compiling instruction " + ins, e);
      }
      
      if(ins.getLineNumber() != null) {
        mv.visitLineNumber(ins.getLineNumber(), insLabel);
      }
    }
  }

  private void emitSwitch(GimpleSwitch ins) {
    SimpleExpr valueGenerator = exprFactory.findValueGenerator(ins.getValue());
    valueGenerator.load(mv);
    Label defaultLabel = labels.of(ins.getDefaultCase().getBasicBlockIndex());

    int numCases = ins.getCaseCount();
    Label[] caseLabels = new Label[numCases];
    int[] caseValues = new int[numCases];

    int i = 0;
    for (GimpleSwitch.Case aCase : ins.getCases()) {
      for(int value = aCase.getLow(); value <= aCase.getHigh(); ++value) {
        caseLabels[i] = labels.of(aCase.getBasicBlockIndex());
        caseValues[i] = value;
        i++;
      }
    }
    mv.visitLookupSwitchInsn(defaultLabel, caseValues, caseLabels);
    
  }

  private void emitAssignment(GimpleAssignment ins) {
    try {
      Expr lhs = exprFactory.findGenerator(ins.getLHS());
      Expr rhs = exprFactory.findGenerator(ins.getOperator(), ins.getOperands(), ins.getLHS().getType());
      
      if(!(lhs instanceof LValue)) {
        throw new InternalCompilerException(ins.getLHS() + " is not an LHS expression: " + lhs.getClass().getName());
      }

      ((LValue) lhs).store(mv, rhs);
      
    } catch (Exception e) {
      throw new RuntimeException("Exception compiling assignment to " + ins, e);
    }
  }

  private void emitGoto(GimpleGoto ins) {
    mv.visitJumpInsn(GOTO, labels.of(ins.getTarget()));
  }

  private void emitConditional(GimpleConditional ins) {
    ConditionGenerator generator = exprFactory.findConditionGenerator(ins.getOperator(), ins.getOperands());
        
    generator.emitJump(mv, labels.of(ins.getTrueLabel()), labels.of(ins.getFalseLabel()));
  }


  private void emitCall(GimpleCall ins) {
    CallGenerator callGenerator = exprFactory.findCallGenerator(ins.getFunction(), ins.getOperands());
    callGenerator.emitCall(mv, exprFactory, ins);
  }


  private void emitReturn(GimpleReturn ins) {
    if(function.getReturnType() instanceof GimpleVoidType) {
      mv.areturn(Type.VOID_TYPE);
    } else {
      SimpleExpr returnValue;
      if(ins.getValue() == null) {
        returnValue = returnStrategy.getDefaultReturnValue();
      } else {
        Expr returnExpr = exprFactory.findGenerator(ins.getValue(), function.getReturnType());
        returnValue = returnStrategy.marshall(returnExpr);
      }
      returnValue.load(mv);
      mv.areturn(returnValue.getType());
    }
  }

  public String getFunctionDescriptor() {
    return TypeOracle.getMethodDescriptor(returnStrategy, getParamStrategies());
  }
  
  @Override
  public List<ParamStrategy> getParamStrategies() {
    List<ParamStrategy> parameterTypes = new ArrayList<ParamStrategy>();
    for (GimpleParameter parameter : function.getParameters()) {
      ParamStrategy generator = params.get(parameter);
      parameterTypes.add(generator);
    }
    return parameterTypes;
  }

  @Override
  public boolean isVarArgs() {
    return false;
  }

  public Type returnType() {
    return returnStrategy.getType();
  }

  public ReturnStrategy getReturnStrategy() {
    return returnStrategy;
  }

  @Override
  public void invoke(MethodGenerator mv) {
    mv.invokestatic(getClassName(), function.getMangledName(), getFunctionDescriptor(), false);
  }

  public GimpleCompilationUnit getCompilationUnit() {
    return function.getUnit();
  }

  @Override
  public Handle getMethodHandle() {
    return new Handle(H_INVOKESTATIC, className, function.getMangledName(), getFunctionDescriptor());
  }

  public String getClassName() {
    return className;
  }

  @Override
  public String toString() {
    return className + "." + getMangledName() + "()";
  }

  public void collectUndefinedSymbols(UndefinedCollector undefined) {
    for (GimpleBasicBlock basicBlock : function.getBasicBlocks()) {
      for (GimpleStatement gimpleStatement : basicBlock.getStatements()) {
        for (GimpleFunctionRef functionRef : gimpleStatement.findUses(GimpleFunctionRef.class)) {
          if (!symbolTable.isFunctionDefined(functionRef.getName())) {
            undefined.add(functionRef.getName(), function);
          }
        }
      }
    }
  }
}
