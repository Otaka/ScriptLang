package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class LoadLocalVarInstruction extends BaseInstruction {

    private int localVarIndex;

    public LoadLocalVarInstruction(int localVarIndex) {
        this.localVarIndex = localVarIndex;
    }

    public int getLocalVarIndex() {
        return localVarIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int index = interpreter.bp + localVarIndex + 1;
        interpreter.addToStack(interpreter.stack[index]);
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "LocalVar " + localVarIndex;
    }

}
