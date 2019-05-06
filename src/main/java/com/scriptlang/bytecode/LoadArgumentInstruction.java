package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class LoadArgumentInstruction extends BaseInstruction {

    private int argumentIndex;

    public LoadArgumentInstruction(int argumentIndex) {
        this.argumentIndex = argumentIndex;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int stackIndex = interpreter.bp - (3 + argumentIndex);
        interpreter.addToStack(interpreter.stack[stackIndex]);
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Arg " + argumentIndex;
    }
}
