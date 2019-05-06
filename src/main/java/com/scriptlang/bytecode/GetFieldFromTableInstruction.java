package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import com.scriptlang.exceptions.ExecutionException;
import java.util.HashMap;

/**
 * @author Dmitry
 */
public class GetFieldFromTableInstruction extends BaseInstruction {

    @Override
    public void execute(Interpreter interpreter) {
        MutableVar key = (MutableVar) interpreter.stack[interpreter.sp];
        MutableVar owner = (MutableVar) interpreter.stack[interpreter.sp - 1];
        interpreter.sp--;
        interpreter.ip++;
        if (owner.variableType != VariableType.TABLE) {
            throw new ExecutionException("Cannot extract field from [" + owner + "] variable. You can do this only from table");
        }
        HashMap table = (HashMap) owner.value;

        interpreter.stack[interpreter.sp] = (MutableVar) table.get(key);
    }

}
