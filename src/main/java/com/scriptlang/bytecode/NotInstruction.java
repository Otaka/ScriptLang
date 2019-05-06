package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class NotInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar value = (MutableVar) interpreter.stack[interpreter.sp];
        if (!(value.variableType == VariableType.BOOLEAN)) {
            throw new ExecutionException("Not operation can be done only on booleans, but found [" + value + "]");
        }
        MutableVar booleanValue = new MutableVar(!value.getAsBoolean());
        interpreter.stack[interpreter.sp] = booleanValue;
        interpreter.ip++;
    }

}
