package com.scriptlang.bytecode;

import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class LessOrEqualInstruction extends MoreThanInstruction {

    @Override
    protected MutableVar createMutableBoolean(boolean value) {
        return super.createMutableBoolean(!value);
    }

    @Override
    public String toString() {
        return "LessOrEquals";
    }
}
