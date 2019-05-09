package com.scriptlang;

import com.scriptlang.bytecode.BaseInstruction;
import com.scriptlang.compiler.GlobalVariable;
import com.scriptlang.data.MutableVar;
import com.scriptlang.ffi.ExternalFunctionManager;
import java.io.IOException;
import java.util.Map;

/**
 *
 * Stack layout of function call            <br>
 * return value0                            <br>
 * return value1                            <br>
 * function arg2                            <br>
 * function arg1                            <br>
 * function arg0                            <br>
 * ip                                       <br>
 * bp                                       <br>
 * callinfo -bp Start of function frame    <br>
 * localvar0                                <br>
 * localvar1                                <br>
 * localvar2                                <br>
 *
 * <br>
 * call info - 32 bit value and has following layout<br>
 * 0-3 bits - 4 bit number that specifies arguments count (maximum 15
 * arguments)<br>
 * 4-18 - every bit specifies if current argument is present in current call
 * <br>
 * 19-21 - 3 bits number that specifies how many return values caller side
 * expects(maximum 7 values)<br>
 */
public class Interpreter {

    public int ip;
    public Object[] stack = new Object[1000];
    public int sp = -1;
    public int bp = -1;
    public BaseInstruction[] instructions;
    public String[] stringPool;
    public Map<String, Integer> functionName2IP;
    public ExternalFunctionManager platformFunctions = new ExternalFunctionManager();
    public GlobalVariable[] globalVariables;

    public void compileAndExecute(String resourcePath) throws IOException {
        Compiler compiler = new Compiler();
        compiler.setPlatformFunctions(platformFunctions);
        CompiledScriptFile compiledScript = compiler.compileFile(resourcePath);
        execute(compiledScript);
    }

    public ExternalFunctionManager getPlatformFunctions() {
        return platformFunctions;
    }

    public void execute(CompiledScriptFile compiledScriptFile) {
        Integer ipOfMainFunction = compiledScriptFile.getFunctionName2IP().get("main");
        if (ipOfMainFunction == null) {
            throw new IllegalStateException("Cannot interpret script file [" + compiledScriptFile.getScriptFileName() + "] because it does not have main function");
        }

        globalVariables = compiledScriptFile.getGlobalVariables();
        stringPool = compiledScriptFile.getStringPool();
        this.functionName2IP = compiledScriptFile.getFunctionName2IP();

        initGlobalVariables();

        ip = ipOfMainFunction;
        instructions = compiledScriptFile.getInstructions();
        pushMainFunction();
        executionLoop();
    }

    private void initGlobalVariables() {
        for (GlobalVariable globalVar : globalVariables) {
            if (globalVar.getInitInstructions() == null || globalVar.getInitInstructions().isEmpty()) {
                globalVar.setContent(new MutableVar(0));
            } else {
                instructions = globalVar.getInitInstructions().toArray(new BaseInstruction[0]);
                ip = 0;
                while (ip < instructions.length) {
                    BaseInstruction instruction = instructions[ip];
                    instruction.execute(this);
                }

                globalVar.setContent((MutableVar) stack[sp]);
                sp--;
                if(sp>=0){
                    throw new IllegalStateException("Error while initialize global variable ["+globalVar.getName()+"]. Stack is not in consistent state. Left ["+sp+"] elements on it");
                }
            }
        }
    }

    private void pushMainFunction() {
        addToStack(-1);
        addToStack(-1);
        addToStack(createCallInfo((short) 0, (short) 0, (short) 0));
        bp = sp;
    }

    private void executionLoop() {
        while (ip != -1) {
            BaseInstruction instruction = instructions[ip];
            instruction.execute(this);
        }
    }

    public void addToStack(Object obj) {
        sp++;
        stack[sp] = obj;
    }

    public static int getExpectedArgsCountFromCallInfo(int callInfo) {
        return callInfo & 0b1111;
    }

    public static int getProvidedArgsCountFromCallInfo(int callInfo) {
        return (callInfo >> 4) & 0b1111;
    }

    public static int getReturnValuesFromCallInfo(int callInfo) {
        return (callInfo >> 18) & 0b111;
    }

    public static int createCallInfo(short argsCount, short providedArgumentsLayout, short returnValuesCount) {
        if (returnValuesCount > 7 || returnValuesCount < 0) {
            throw new IllegalStateException("Return values count should be in range [0-7]. Provided [" + returnValuesCount + "]");
        }

        if (argsCount > 15 || argsCount < 0) {
            throw new IllegalStateException("Arguments count should be in range [0-15]. Provided [" + argsCount + "]");
        }
        int result = argsCount & 0b1111;
        result = result | ((providedArgumentsLayout & 0x7F) << 4);
        result = result | ((returnValuesCount & 0b11) << 18);
        return result;
    }

    /*public DecimalData createDecimalDataObject(long value) {
        if (value >= 0) {
            if (value <= Byte.MAX_VALUE) {
                return new MutableByte((byte) value);
            }
            if (value <= Short.MAX_VALUE) {
                return new MutableShort((short) value);
            }
            if (value <= Integer.MAX_VALUE) {
                return new MutableInt((int) value);
            }
            return new MutableLong(value);
        }else{
            if (value >= Byte.MIN_VALUE) {
                return new MutableByte((byte) value);
            }
            if (value >= Short.MIN_VALUE) {
                return new MutableShort((short) value);
            }
            if (value >= Integer.MIN_VALUE) {
                return new MutableInt((int) value);
            }
            return new MutableLong(value);
        }
    }*/
}
