package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class SumInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.STRING || right.variableType == VariableType.STRING) {
            interpreter.stack[interpreter.sp] =new MutableVar(left.getAsString().concat(right.getAsString()));
            return;
        }
        if (left.variableType == VariableType.BOOLEAN || right.variableType == VariableType.BOOLEAN) {
            throw new ExecutionException("Cannot add boolean values. Left value [" + left + "]. Right value [" + right + "]");
        }
        if (left.variableType == VariableType.DOUBLE || right.variableType == VariableType.DOUBLE) {
            double result = left.getAsDouble() + right.getAsDouble();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            long result = left.getAsLong() + right.getAsLong();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }

        throw new ExecutionException("Cannot summ two values of the following types: [" + left.variableType + "] and [" + right.variableType + "]");
    }

    @Override
    public String toString() {
        return "Summ";
    }
}
