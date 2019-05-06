package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class LoadBooleanInstruction extends BaseInstruction {

    private boolean value;

    public LoadBooleanInstruction(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public void execute(Interpreter interpreter) {
        interpreter.sp++;
        interpreter.stack[interpreter.sp] = new MutableVar(value);
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "" + value;
    }

}
