package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class DivInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.STRING || right.variableType == VariableType.STRING) {
            throw new ExecutionException("Cannot divide string [" + left + "] on string [" + right + "]");
        }
        if (left.variableType == VariableType.BOOLEAN || right.variableType == VariableType.BOOLEAN) {
            throw new ExecutionException("Cannot subtract boolean values. Left value [" + left + "]. Right value [" + right + "]");
        }
        if (left.variableType == VariableType.DOUBLE || right.variableType == VariableType.DOUBLE) {
            double result = left.getAsDouble() / right.getAsDouble();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            long leftVal = left.getAsLong();
            long rightVal = right.getAsLong();
            long mod = leftVal % rightVal;
            if (mod != 0) {
                interpreter.stack[interpreter.sp] = new MutableVar((double) leftVal / (double) rightVal);
            } else {
                interpreter.stack[interpreter.sp] = new MutableVar(leftVal / rightVal);
            }
            return;
        }

        throw new ExecutionException("Cannot divide two values of the following types: [" + left.variableType + "] and [" + right.variableType + "]");
    }

    @Override
    public String toString() {
        return "Div";
    }
}
