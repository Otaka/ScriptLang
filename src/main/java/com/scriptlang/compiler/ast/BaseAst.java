package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class BaseAst {

    private int startPosition;
    private int endPosition;

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

}
