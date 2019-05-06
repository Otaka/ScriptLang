package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class LoadGlobalVarInstruction extends BaseInstruction {

    private int globalVarIndex;

    public LoadGlobalVarInstruction(int globalVarIndex) {
        this.globalVarIndex = globalVarIndex;
    }

    public int getGlobalVarIndex() {
        return globalVarIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        interpreter.addToStack(interpreter.stack[globalVarIndex]);
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Global " + globalVarIndex;
    }

}
