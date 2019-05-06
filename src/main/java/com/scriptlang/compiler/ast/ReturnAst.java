package com.scriptlang.compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ReturnAst extends BaseAst {

    private List<BaseAst> expressions = new ArrayList<>();

    public void addReturnExpression(BaseAst expression) {
        expressions.add(expression);
    }

    public List<BaseAst> getExpressions() {
        return expressions;
    }

}
