package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class AssignAst extends BaseAst {

    private BaseAst variable;
    private BaseAst expression;

    public void setExpression(BaseAst expression) {
        this.expression = expression;
    }

    public void setVariable(BaseAst variable) {
        this.variable = variable;
    }

    public BaseAst getExpression() {
        return expression;
    }

    public BaseAst getVariable() {
        return variable;
    }

}
