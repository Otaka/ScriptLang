package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class VariableUsageAst extends BaseAst {

    private String variableName;

    public VariableUsageAst() {
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

}
