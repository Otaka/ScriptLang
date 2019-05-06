package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class JumpFalseInstruction extends JumpTrueInstruction {

    public JumpFalseInstruction(int relativeOffset) {
        super(relativeOffset);
    }

    @Override
    protected boolean getBooleanValue(Interpreter interpreter) {
        return !super.getBooleanValue(interpreter);
    }

}
