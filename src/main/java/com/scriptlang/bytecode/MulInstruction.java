package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class MulInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.STRING) {
            if (right.variableType == VariableType.INT) {
                String leftString = left.getAsString();
                int multiplier = (int) right.getAsLong();
                StringBuilder sb = new StringBuilder((leftString.length() * multiplier));
                for (int i = 0; i < multiplier; i++) {
                    sb.append(leftString);
                }
                interpreter.stack[interpreter.sp] = sb.toString();
            } else {
                throw new ExecutionException("Cannot multiple string [" + left + "] on [" + right + "]");
            }

            return;
        }
        if (left.variableType == VariableType.BOOLEAN || right.variableType == VariableType.BOOLEAN) {
            throw new ExecutionException("Cannot multiple boolean values. Left value [" + left + "]. Right value [" + right + "]");
        }
        if (left.variableType == VariableType.DOUBLE || right.variableType == VariableType.DOUBLE) {
            double result = left.getAsDouble() * right.getAsDouble();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            long result = left.getAsLong() * right.getAsLong();
            interpreter.stack[interpreter.sp] = new MutableVar(result);
            return;
        }

        throw new ExecutionException("Cannot multiple two values of the following types: [" + left + "] and [" + right + "]");
    }

    @Override
    public String toString() {
        return "Mul";
    }
}
