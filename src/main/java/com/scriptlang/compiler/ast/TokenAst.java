package com.scriptlang.compiler.ast;

/**
 * @author sad
 */
public class TokenAst extends BaseAst {

    private String value;

    public TokenAst(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
