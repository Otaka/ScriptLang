package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;
import java.util.HashMap;

/**
 * @author Dmitry
 */
public class SetFieldToTableInstruction extends BaseInstruction{

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar assignedValue = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar key = (MutableVar) interpreter.stack[interpreter.sp-1];
        MutableVar owner = (MutableVar) interpreter.stack[interpreter.sp - 2];
        interpreter.sp-=3;
        interpreter.ip++;
        if (owner.variableType != VariableType.TABLE) {
            throw new ExecutionException("Cannot assign to field of [" + owner + "] variable. You can do this only with tables");
        }
        HashMap table = (HashMap) owner.value;
        table.put(key, assignedValue);
    }

}
