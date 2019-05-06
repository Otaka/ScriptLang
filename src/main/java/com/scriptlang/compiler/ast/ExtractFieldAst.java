package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class ExtractFieldAst extends BaseAst {

    private BaseAst ownerObject;
    private BaseAst keyExpression;

    public void setKeyExpression(BaseAst keyExpression) {
        this.keyExpression = keyExpression;
    }

    public void setOwnerObject(BaseAst ownerObject) {
        this.ownerObject = ownerObject;
    }

    public BaseAst getKeyExpression() {
        return keyExpression;
    }

    public BaseAst getOwnerObject() {
        return ownerObject;
    }
}
