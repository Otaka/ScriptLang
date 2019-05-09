package com.scriptlang.compiler;

import com.scriptlang.bytecode.BaseInstruction;
import com.scriptlang.data.MutableVar;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class GlobalVariable extends Variable implements InstructionsContainer {

    private List<BaseInstruction> initInstructions = new ArrayList<>();
    public MutableVar content;

    public GlobalVariable(String name, int index, VariablesScope scope) {
        super(name, VariableType.GLOBAL, index, scope);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MutableVar getContent() {
        return content;
    }

    public void setContent(MutableVar content) {
        this.content = content;
    }

    @Override
    public void addInstruction(BaseInstruction instruction) {
        initInstructions.add(instruction);
    }

    public List<BaseInstruction> getInitInstructions() {
        return initInstructions;
    }

    @Override
    public int instructionsCount() {
        return initInstructions.size();
    }
}
