package com.scriptlang.compiler.ast;

/**
 * @author sad
 */
public class StringAst extends BaseAst {

    private String value;

    public StringAst(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
