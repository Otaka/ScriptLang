package com.scriptlang.compiler.ast;

/**
 * @author sad
 */
public class BooleanValueAst extends BaseAst {

    private boolean value;

    public BooleanValueAst() {
    }

    public BooleanValueAst(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
