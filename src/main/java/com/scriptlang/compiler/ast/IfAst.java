package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class IfAst extends BaseAst {

    private BaseAst conditionAst;
    private ExpressionListAst bodyAst;
    private BaseAst elseAst;

    public BaseAst getConditionAst() {
        return conditionAst;
    }

    public void setConditionAst(BaseAst conditionAst) {
        this.conditionAst = conditionAst;
    }

    public ExpressionListAst getBodyAst() {
        return bodyAst;
    }

    public void setBodyAst(ExpressionListAst bodyAst) {
        this.bodyAst = bodyAst;
    }

    public void setElseAst(BaseAst elseAst) {
        this.elseAst = elseAst;
    }

    public BaseAst getElseAst() {
        return elseAst;
    }

}
