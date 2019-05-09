package com.scriptlang.compiler;

/**
 * @author Dmitry
 */
public class Variable {

    private String name;
    private VariableType variableType;
    protected int index;
    private VariablesScope scope;

    public Variable(String name, VariableType variableType, int index, VariablesScope scope) {
        this.name = name;
        this.variableType = variableType;
        this.index = index;
        this.scope = scope;
    }

    public VariablesScope getScope() {
        return scope;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public static enum VariableType {
        GLOBAL, LOCAL, ARG
    }
}
