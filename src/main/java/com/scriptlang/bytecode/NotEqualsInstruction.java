package com.scriptlang.bytecode;

import com.scriptlang.data.MutableVar;

/**
 * @author Dmitry
 */
public class NotEqualsInstruction extends EqualsInstruction {

    @Override
    protected MutableVar createMutableBoolean(boolean value) {
        return super.createMutableBoolean(!value);
    }
}
