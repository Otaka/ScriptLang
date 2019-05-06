package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class LocalFunctionCallInstruction extends BaseInstruction {

    private int address;
    private int argsCount;
    private int waitingForReturnVariablesCount;

    public LocalFunctionCallInstruction(int address, int argsCount, int waitingForReturnVariablesCount) {
        this.address = address;
        this.argsCount = argsCount;
        this.waitingForReturnVariablesCount = waitingForReturnVariablesCount;
    }

    @Override
    public void execute(Interpreter interpreter) {
        //interpreter.sp += waitingForReturnVariablesCount; /fx it is already implemented with separated instruction AddTOStackInstruction
        interpreter.addToStack(interpreter.ip);
        interpreter.addToStack(interpreter.bp);
        int callInfo = Interpreter.createCallInfo((short) argsCount, (short) argsCount, (short) waitingForReturnVariablesCount);
        interpreter.addToStack(callInfo);
        interpreter.bp = interpreter.sp;
        interpreter.ip = address;
    }

    @Override
    public String toString() {
        return "Call [" + getComment() + "]";
    }

}
