package com.scriptlang.compiler.ast;

/**
 * @author sad
 */
public class LongValueAst extends BaseAst {

    private long value;

    public LongValueAst(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
