package com.scriptlang.compiler;

import com.scriptlang.compiler.ast.NamedFunctionAst;
import com.scriptlang.ffi.ExternalFunctionManager;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class BytecodeGenerationContext {

    private List<NamedFunctionAst> foundFunctions = new ArrayList<>();

    private List<VariablesScope> scopes = new ArrayList<>();
    private List<String> stringPool = new ArrayList<>();
    private ExternalFunctionManager externalFunctionManager;

    public BytecodeGenerationContext(ExternalFunctionManager platformFunctions) {
        this.externalFunctionManager = platformFunctions;
        addNewScope();
    }

    public ExternalFunctionManager getExternalFunctionManager() {
        return externalFunctionManager;
    }

    public int addStringToPoolIfNotExists(String str) {
        int index = stringPool.indexOf(str);
        if (index == -1) {
            stringPool.add(str);
            index = stringPool.size() - 1;
        }
        return index;
    }

    public List<String> getStringPool() {
        return stringPool;
    }

    public NamedFunctionAst searchFunction(String name) {
        for (NamedFunctionAst function : foundFunctions) {
            if (function.getFunctionName().equals(name)) {
                return function;
            }
        }
        return null;
    }

    public List<NamedFunctionAst> getFoundFunctions() {
        return foundFunctions;
    }

    public void addNewScope() {
        scopes.add(new VariablesScope());
    }

    public void removeScope() {
        scopes.remove(scopes.size() - 1);
    }

    public Variable searchVariable(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            VariablesScope scope = scopes.get(i);
            Variable v = scope.findByName(name);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    public VariablesScope getCurrentScope() {
        return scopes.get(scopes.size() - 1);
    }

    public void addVariable(Variable variable) {
        getCurrentScope().getVariables().add(variable);
    }
}
