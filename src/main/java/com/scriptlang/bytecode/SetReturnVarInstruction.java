package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class SetReturnVarInstruction extends BaseInstruction {

    private int returnVarInstructionIndex;

    public SetReturnVarInstruction(int returnVarInstruction) {
        this.returnVarInstructionIndex = returnVarInstruction;
    }

    @Override
    public void execute(Interpreter interpreter) {
        int callInfo = (int) interpreter.stack[interpreter.bp];
        int callerExpectsReturnValues = Interpreter.getReturnValuesFromCallInfo(callInfo);
        if (returnVarInstructionIndex <= (callerExpectsReturnValues - 1)) {
            int actualArgsCount = Interpreter.getProvidedArgsCountFromCallInfo(callInfo);
            int index = interpreter.bp - 2 - actualArgsCount - (callerExpectsReturnValues - returnVarInstructionIndex);
            interpreter.stack[index] = interpreter.stack[interpreter.sp];
        } else {
            //caller does not interesed in this return value. just discard it
        }
        interpreter.sp--;
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Set Return Value " + returnVarInstructionIndex;
    }

}
