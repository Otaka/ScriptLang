package com.scriptlang.compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class ScriptFileAst extends BaseAst {

    private List<VariableDeclarationAst> globalVariables = new ArrayList<>();
    private List<NamedFunctionAst> functions = new ArrayList<>();

    public List<NamedFunctionAst> getFunctions() {
        return functions;
    }

    public List<VariableDeclarationAst> getGlobalVariables() {
        return globalVariables;
    }

    public void addFunction(NamedFunctionAst functionAst) {
        functions.add(functionAst);
    }

    public void addGlobalVar(VariableDeclarationAst variableDeclarationAst) {
        globalVariables.add(variableDeclarationAst);
    }

}
