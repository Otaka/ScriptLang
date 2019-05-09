package com.scriptlang;

import com.scriptlang.bytecode.BaseInstruction;
import com.scriptlang.compiler.GlobalVariable;
import java.util.Map;

/**
 * @author Dmitry
 */
public class CompiledScriptFile {

    private Map<String, Integer> functionName2IP;
    private BaseInstruction[] instructions;
    private String[] stringPool;
    private String scriptFileName;
    private GlobalVariable[] globalVariables;

    public CompiledScriptFile(Map<String, Integer> functionName2IP, BaseInstruction[] instructions, String[] stringPool, GlobalVariable[] globalVariables) {
        this.functionName2IP = functionName2IP;
        this.instructions = instructions;
        this.stringPool = stringPool;
        this.globalVariables = globalVariables;
    }

    public GlobalVariable[] getGlobalVariables() {
        return globalVariables;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public String[] getStringPool() {
        return stringPool;
    }

    public Map<String, Integer> getFunctionName2IP() {
        return functionName2IP;
    }

    public BaseInstruction[] getInstructions() {
        return instructions;
    }

}
