package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class NotAst extends BaseAst {

    private BaseAst expression;

    public void setExpression(BaseAst expression) {
        this.expression = expression;
    }

    public BaseAst getExpression() {
        return expression;
    }

}
