package com.scriptlang.bytecode;

import com.scriptlang.Interpreter;

/**
 * @author Dmitry
 */
public class AddToStackInstruction extends BaseInstruction {

    private int elementsCount;

    public AddToStackInstruction(int elementsCount) {
        this.elementsCount = elementsCount;
    }

    public int getElementsCount() {
        return elementsCount;
    }

    @Override
    public void execute(Interpreter interpreter) {
        //cleaning
        int sp = interpreter.sp + 1;
        for (int i = 0; i < elementsCount; i++, sp++) {
            interpreter.stack[sp] = null;
        }

        interpreter.sp += elementsCount;
        interpreter.ip++;
    }

    @Override
    public String toString() {
        return "Add " + elementsCount + " cells";
    }

}
