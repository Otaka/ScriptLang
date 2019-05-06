package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class SetGlobalVariableInstruction extends BaseInstruction {

    private int globalVariableIndex;

    public SetGlobalVariableInstruction(int globalVariableIndex) {
        this.globalVariableIndex = globalVariableIndex;
    }

    public int getGlobalVariableIndex() {
        return globalVariableIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int index = globalVariableIndex;
        interpreter.stack[index] = interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Set Global " + globalVariableIndex;
    }

}
