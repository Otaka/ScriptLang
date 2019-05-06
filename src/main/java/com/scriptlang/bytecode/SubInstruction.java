package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class SubInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.STRING || right.variableType == VariableType.STRING) {
            interpreter.stack[interpreter.sp] = left.toString().replace(right.toString(), "");
            return;
        }
        if (left.variableType == VariableType.BOOLEAN || right.variableType == VariableType.BOOLEAN) {
            throw new ExecutionException("Cannot subtract boolean values. Left value [" + left + "]. Right value [" + right + "]");
        }
        if (left.variableType == VariableType.DOUBLE || right.variableType == VariableType.DOUBLE) {
            double result = left.getAsDouble() - right.getAsDouble();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            long result = left.getAsLong() - right.getAsLong();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }

        throw new ExecutionException("Cannot subtract following two values: [" + left + "] and [" + right + "]");
    }

    @Override
    public String toString() {
        return "Sub";
    }
}
