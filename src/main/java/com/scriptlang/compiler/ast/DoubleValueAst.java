package com.scriptlang.compiler.ast;

/**
 * @author sad
 */
public class DoubleValueAst extends BaseAst {

    private double value;

    public DoubleValueAst(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
