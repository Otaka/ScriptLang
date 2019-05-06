package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

public class ReturnInstruction extends BaseInstruction {

    private int localVarToRemove;

    public ReturnInstruction(int localVarToRemove) {
        this.localVarToRemove = localVarToRemove;
    }

    public int getLocalVarToRemove() {
        return localVarToRemove;
    }

    public void setLocalVarToRemove(int localVarToRemove) {
        this.localVarToRemove = localVarToRemove;
    }

    @Override
    public void execute(Interpreter interpreter) {
        //remove local variables
        interpreter.sp -= localVarToRemove;
        int callInfo = (int) interpreter.stack[interpreter.sp];
        int argsCount = Interpreter.getProvidedArgsCountFromCallInfo(callInfo);
        int oldBp = (int) interpreter.stack[interpreter.sp - 1];
        int oldIp = (int) interpreter.stack[interpreter.sp - 2];
        //remove callInfo, bp and sp and arguments
        interpreter.sp -= (3 + argsCount);
        interpreter.bp = oldBp;
        if (oldIp != -1) {
            interpreter.ip = oldIp + 1;
        } else {
            interpreter.ip = -1;//finish execution
        }
    }

    @Override
    public String toString() {
        return "Return";
    }

}
