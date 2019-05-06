package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;
import com.scriptlang.data.MutableVar;
import com.scriptlang.exceptions.ExecutionException;
import com.scriptlang.ffi.ExternalFunctionManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * @author Dmitry
 */
public class ExternalFunctionCallInstruction extends BaseInstruction {

    private final static Object[] emptyArray = new Object[0];
    private String functionName;
    private int expectedReturnValuesCount;
    private ExternalFunctionManager.ExternalFunctionData externalFunctionData;

    public ExternalFunctionCallInstruction(String functionName, int expectedReturnValuesCount, ExternalFunctionManager.ExternalFunctionData externalFunctionData) {
        this.functionName = functionName;
        this.expectedReturnValuesCount = expectedReturnValuesCount;
        this.externalFunctionData = externalFunctionData;
    }

    @Override
    public void execute(Interpreter interpreter) {
        try {
            Object[] arguments = convertArguments(interpreter);
            interpreter.sp -= externalFunctionData.getArgumentsCount();
            interpreter.ip++;
            Object result = externalFunctionData.getMethod().invoke(externalFunctionData.getOwnerObject(), arguments);
            if (expectedReturnValuesCount > 0) {
                if (externalFunctionData.getMethod().getReturnType() != Void.class) {
                    MutableVar returnValue = convertJavaToMutableVar(result);
                    interpreter.stack[interpreter.sp]=returnValue;
                }else{
                    interpreter.stack[interpreter.sp]=new MutableVar(0);
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ExecutionException("Error while executing function [" + functionName + "]", ex);
        }
    }

    private MutableVar convertJavaToMutableVar(Object value) {
        if (value instanceof String) {
            return new MutableVar((String) value);
        }
        if (value instanceof Long) {
            return new MutableVar((Long) value);
        }
        if (value instanceof Double) {
            return new MutableVar((Double) value);
        }
        if (value instanceof Integer) {
            return new MutableVar((long) (int) value);
        }
        if (value instanceof Boolean) {
            return new MutableVar((boolean) value);
        }
        if (value instanceof HashMap) {
            return new MutableVar((HashMap) value);
        }
        if (value instanceof Float) {
            return new MutableVar((double) (float) value);
        }
        if (value instanceof MutableVar) {
            return (MutableVar) value;
        }
        if (value instanceof Short) {
            return new MutableVar((long) (short) value);
        }
        if (value instanceof Byte) {
            return new MutableVar((long) (byte) value);
        }

        throw new ExecutionException("External function [" + functionName + "] returned data that cannot be used by scripting engine [" + value.getClass().getName() + "]");
    }

    private Object[] convertArguments(Interpreter interpreter) {
        if (externalFunctionData.getArgumentsCount() == 0) {
            return emptyArray;
        }
        Object[] arguments = new Object[externalFunctionData.getArgumentsCount()];
        for (int i = 0; i < externalFunctionData.getArgumentsCount(); i++) {
            MutableVar argument = (MutableVar) interpreter.stack[interpreter.sp - i];
            arguments[i] = argument.value;
        }

        return arguments;
    }
}
