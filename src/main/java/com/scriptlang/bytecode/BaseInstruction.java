package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public abstract class BaseInstruction {

    private String comment;

    public BaseInstruction setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public abstract void execute(Interpreter interpreter);
}
