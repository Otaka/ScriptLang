package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class SetArgumentVariableInstruction extends BaseInstruction {

    private int argumentIndex;

    public SetArgumentVariableInstruction(int argumentIndex) {
        this.argumentIndex = argumentIndex;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int index = interpreter.bp - (3 + argumentIndex);
        interpreter.stack[index] = interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Set Arg " + argumentIndex;
    }

}
