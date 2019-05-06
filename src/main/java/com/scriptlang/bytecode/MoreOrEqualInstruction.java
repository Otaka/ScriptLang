package com.scriptlang.bytecode;

import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class MoreOrEqualInstruction extends LessThanInstruction {

    @Override
    protected MutableVar createMutableBoolean(boolean value) {
        return super.createMutableBoolean(!value);
    }

    @Override
    public String toString() {
        return "MoreOrEquals";
    }
}
