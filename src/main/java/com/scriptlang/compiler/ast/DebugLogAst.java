package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class DebugLogAst extends BaseAst {

    private BaseAst key;
    private BaseAst expression;

    public void setExpression(BaseAst expression) {
        this.expression = expression;
    }

    public void setKey(BaseAst key) {
        this.key = key;
    }

    public BaseAst getKey() {
        return key;
    }

    public BaseAst getExpression() {
        return expression;
    }
}
