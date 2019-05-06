package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class JumpRelInstruction extends AbstractRelativeJumpInstruction {

    public JumpRelInstruction(int offset) {
        setOffset(offset);
    }

    @Override
    public void execute(Interpreter interpreter) {
        interpreter.ip += getOffset();
    }

    @Override
    public String toString() {
        return "Jump " + getOffset();
    }

}
