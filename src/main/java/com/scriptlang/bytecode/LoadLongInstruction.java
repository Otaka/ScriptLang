package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class LoadLongInstruction extends BaseInstruction {

    private long value;

    public LoadLongInstruction(long value) {
        this.value = value;
    }

    public LoadLongInstruction() {
    }

    public long getValue() {
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
