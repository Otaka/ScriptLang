package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class ModInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar left = (MutableVar) interpreter.stack[interpreter.sp - 1];
        MutableVar right = (MutableVar) interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
        if (left.variableType == VariableType.INT || right.variableType == VariableType.INT) {
            long leftVal = left.getAsLong();
            long rightVal = right.getAsLong();
            long mod = leftVal % rightVal;
            interpreter.stack[interpreter.sp] = new MutableVar(mod);
            return;
        }

        throw new ExecutionException("Cannot calculate mod of two values of the following types: [" + left + "] and [" + right + "]");
    }

    @Override
    public String toString() {
        return "Mod";
    }

}
