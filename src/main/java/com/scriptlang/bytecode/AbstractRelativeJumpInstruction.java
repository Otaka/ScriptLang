package com.scriptlang.bytecode;

/**
 * @author Dmitry
 */
public abstract class AbstractRelativeJumpInstruction extends BaseInstruction {

    private int offset;

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

}
