package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class SetLocalVariableInstruction extends BaseInstruction {

    private int localVariableIndex;

    public SetLocalVariableInstruction(int localVariableIndex) {
        this.localVariableIndex = localVariableIndex;
    }

    public int getLocalVariableIndex() {
        return localVariableIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int index = interpreter.bp + 1 + localVariableIndex;
        interpreter.stack[index] = interpreter.stack[interpreter.sp];
        interpreter.sp--;
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Set Local " + localVariableIndex;
    }

}
