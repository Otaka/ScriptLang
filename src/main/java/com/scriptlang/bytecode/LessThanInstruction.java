package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class LessThanInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.STRING && right.variableType == VariableType.STRING) {
            int compareResult = left.toString().compareTo(right.toString());
            interpreter.stack[interpreter.sp] = createMutableBoolean(compareResult < 0);
            return;
        }

        if ((left.variableType == VariableType.DOUBLE && right.variableType == VariableType.DOUBLE)
                || ((left.variableType == VariableType.DOUBLE || right.variableType == VariableType.DOUBLE) && (left.variableType == VariableType.INT || right.variableType == VariableType.INT))) {
            double leftВ = left.getAsDouble();
            double rightВ = right.getAsDouble();
            boolean result = leftВ < rightВ;
            interpreter.stack[interpreter.sp] = createMutableBoolean(result);
            return;
        }
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            boolean result = left.getAsLong() < right.getAsLong();
            interpreter.stack[interpreter.sp] = createMutableBoolean(result);
            return;
        }

        throw new ExecutionException("Cannot compare two values of the following types: [" + left.getClass().getSimpleName() + "] and [" + right.getClass().getSimpleName() + "]");
    }

    protected MutableVar createMutableBoolean(boolean value) {
        return new MutableVar(value);
    }

    @Override
    public String toString() {
        return "LessThan";
    }

}
