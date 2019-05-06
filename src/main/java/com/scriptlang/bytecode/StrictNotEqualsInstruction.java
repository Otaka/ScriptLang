package com.scriptlang.bytecode;

import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class StrictNotEqualsInstruction extends StrictEqualsInstruction {

    @Override
    protected MutableVar createMutableBoolean(boolean value) {
        return super.createMutableBoolean(!value);
    }

    @Override
    public String toString() {
        return "StrictNotEquals";
    }

}
