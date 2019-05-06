package com.scriptlang.compiler.ast;

import com.scriptlang.bytecode.BaseInstruction;
import com.scriptlang.compiler.InstructionsContainer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class NamedFunctionAst extends BaseAst implements InstructionsContainer {

    private String functionName;
    private List<String> args = new ArrayList<>();
    private ExpressionListAst expressions;
    private List<BaseInstruction> compiledInstructions = new ArrayList<>();
    private int localVariablesCount;

    public void setLocalVariablesCount(int localVariablesCount) {
        this.localVariablesCount = localVariablesCount;
    }

    public int getLocalVariablesCount() {
        return localVariablesCount;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void addArg(String name) {
        args.add(name);
    }

    public void setExpressions(ExpressionListAst expressions) {
        this.expressions = expressions;
    }

    public List<String> getArgs() {
        return args;
    }

    public ExpressionListAst getExpressions() {
        return expressions;
    }

    @Override
    public void addInstruction(BaseInstruction instruction) {
        compiledInstructions.add(instruction);
    }

    @Override
    public int instructionsCount() {
        return compiledInstructions.size();
    }

    public List<BaseInstruction> getCompiledInstructions() {
        return compiledInstructions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(functionName).append("(");
        boolean first = true;
        for (String argName : args) {
            if (first == false) {
                sb.append(",");
            }
            first = false;
            sb.append(argName);
        }
        sb.append(")");
        return sb.toString();
    }
}
