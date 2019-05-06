package com.scriptlang.compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ExpressionListAst extends BaseAst {

    private List<BaseAst> expressions = new ArrayList<>();

    public void addExpression(BaseAst ast) {
        expressions.add(ast);
    }

    public List<BaseAst> getExpressions() {
        return expressions;
    }

}
