package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class LoadDoubleInstruction extends BaseInstruction {

    private double value;

    public LoadDoubleInstruction(double value) {
        this.value = value;
    }

    public double getValue() {
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
