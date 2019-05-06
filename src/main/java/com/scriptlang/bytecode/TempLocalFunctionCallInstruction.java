package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.compiler.ast.NamedFunctionAst;

/**
 * @author Dmitry
 */
public class TempLocalFunctionCallInstruction extends BaseInstruction {

    private NamedFunctionAst functionToCall;
    private int waitingForReturnVariablesCount;

    public TempLocalFunctionCallInstruction(NamedFunctionAst functionToCall, int waitingForReturnVariablesCount) {
        this.functionToCall = functionToCall;
        this.waitingForReturnVariablesCount = waitingForReturnVariablesCount;
    }

    public int getWaitingForReturnVariablesCount() {
        return waitingForReturnVariablesCount;
    }

    public NamedFunctionAst getFunctionToCall() {
        return functionToCall;
    }

    @Override
    public void execute(Interpreter interpreter) {
        throw new UnsupportedOperationException("This is temporary instruction, it should not be executed");
    }

    @Override
    public String toString() {
        return "Temp Call " + functionToCall.getFunctionName() + "=>" + waitingForReturnVariablesCount;
    }

}
