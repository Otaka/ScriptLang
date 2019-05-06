package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class DiscardStackInstruction extends BaseAst {

    private int count;

    public DiscardStackInstruction(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}
