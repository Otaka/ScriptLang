package com.scriptlang.compiler;

import com.scriptlang.bytecode.BaseInstruction;

/**
 * @author Dmitry
 */
public interface InstructionsContainer {

    public void addInstruction(BaseInstruction instruction);

    public int instructionsCount();
}
