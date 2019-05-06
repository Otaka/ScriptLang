package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class BinaryExpressionAst extends BaseAst {

    private String operation;
    private BaseAst left;
    private BaseAst right;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public BaseAst getLeft() {
        return left;
    }

    public void setLeft(BaseAst left) {
        this.left = left;
    }

    public BaseAst getRight() {
        return right;
    }

    public void setRight(BaseAst right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "X " + operation + " Y";
    }
}
