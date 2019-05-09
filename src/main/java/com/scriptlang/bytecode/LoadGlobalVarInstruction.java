package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.compiler.GlobalVariable;
import com.scriptlang.exceptions.ExecutionException;

/**
 * @author Dmitry
 */
public class LoadGlobalVarInstruction extends BaseInstruction {

    private int globalVariableNameId;
    private int globalVariableIndex = -1;

    public LoadGlobalVarInstruction(int globalVariableNameId) {
        this.globalVariableNameId = globalVariableNameId;
    }

    public int getGlobalVariableNameId() {
        return globalVariableNameId;
    }

    @Override
    public void execute(Interpreter interpreter) {
        if (globalVariableIndex == -1) {
            String globalVarName = interpreter.stringPool[globalVariableNameId];
            GlobalVariable var = findGlobalVariable(interpreter, globalVarName);
            if (var == null) {
                throw new ExecutionException("Runtime error. Cannot find global variable with name [" + globalVarName + "]");
            }

            globalVariableIndex = var.getIndex();
        }
        interpreter.addToStack(interpreter.globalVariables[globalVariableIndex].content);
        interpreter.ip++;
    }

    private GlobalVariable findGlobalVariable(Interpreter interpreter, String name) {
        for (GlobalVariable var : interpreter.globalVariables) {
            if (var.getName().equals(name)) {
                return var;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Global " + globalVariableNameId;
    }

}
