package com.scriptlang.compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class VariableDeclarationAst extends BaseAst {

    private List<String> varNames = new ArrayList<>();
    private BaseAst expression;

    public void setExpression(BaseAst expression) {
        this.expression = expression;
    }

    public void addVarName(String varName) {
        this.varNames.add(varName);
    }

    public List<String> getVarNames() {
        return varNames;
    }

    public BaseAst getExpression() {
        return expression;
    }

}
