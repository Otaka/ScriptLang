package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import java.util.HashMap;

/**
 * @author Dmitry
 */
public class LoadTableInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        interpreter.sp++;
        interpreter.stack[interpreter.sp] = new MutableVar(new HashMap());
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "LoadTable";
    }
}
