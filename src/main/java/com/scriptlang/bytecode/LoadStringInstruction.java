package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class LoadStringInstruction extends BaseInstruction {

    private int constantStringPoolIndex;

    public LoadStringInstruction(int constantStringPoolIndex) {
        this.constantStringPoolIndex = constantStringPoolIndex;
    }

    public int getConstantStringPoolIndex() {
        return constantStringPoolIndex;
    }

    @Override
    public void execute(Interpreter interpreter) {
        interpreter.sp++;
        interpreter.stack[interpreter.sp] = new MutableVar(interpreter.stringPool[constantStringPoolIndex]);
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "string " + constantStringPoolIndex;
    }
}
