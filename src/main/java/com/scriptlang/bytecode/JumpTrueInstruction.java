package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class JumpTrueInstruction extends AbstractRelativeJumpInstruction {

    public JumpTrueInstruction(int relativeOffset) {
        setOffset(relativeOffset);
    }

    protected boolean getBooleanValue(Interpreter interpreter) {
        MutableVar value = (MutableVar) interpreter.stack[interpreter.sp];
        interpreter.sp--;
        if (value.variableType != VariableType.BOOLEAN) {
            throw new ExecutionException("Conditional branch can work only with boolean values, but you have provided [" + value + "]");
        }

        return value.getAsBoolean();
    }

    @Override
    public void execute(Interpreter interpreter) {
        boolean boolValue = getBooleanValue(interpreter);
        if (boolValue) {
            interpreter.ip += getOffset();
        } else {
            interpreter.ip++;
        }
    }
}
