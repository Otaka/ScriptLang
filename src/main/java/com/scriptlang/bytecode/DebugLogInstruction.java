package com.scriptlang.bytecode;

import com.scriptlang.DebugLog;
import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;

/**
 * @author Dmitry
 */
public class DebugLogInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar key = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar value = (MutableVar) interpreter.stack[interpreter.sp - 1];
        interpreter.sp -= 2;
        if (key.variableType != VariableType.STRING) {
            throw new RuntimeException("debuglog statement expects 'key' as string value, but found " + key.variableType);
        }

        DebugLog.getKeyValueMap().put(key.getAsString(), value);
        interpreter.ip++;
    }
}
