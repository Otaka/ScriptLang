package com.scriptlang.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class VariablesScope {

    private List<Variable> variables = new ArrayList<>();

    public List<Variable> getVariables() {
        return variables;
    }

    public Variable findByName(String name) {
        for (Variable v : variables) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }
}
