package com.scriptlang.compiler.ast;

/**
 * @author Dmitry
 */
public class ForAst extends BaseAst {

    private BaseAst initializationExpression;
    private BaseAst conditionExpression;
    private BaseAst incrementExpression;
    private ExpressionListAst body;

    public BaseAst getInitializationExpression() {
        return initializationExpression;
    }

    public void setInitializationExpression(BaseAst initializationExpression) {
        this.initializationExpression = initializationExpression;
    }

    public BaseAst getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(BaseAst conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public BaseAst getIncrementExpression() {
        return incrementExpression;
    }

    public void setIncrementExpression(BaseAst incrementExpression) {
        this.incrementExpression = incrementExpression;
    }

    public ExpressionListAst getBody() {
        return body;
    }

    public void setBody(ExpressionListAst body) {
        this.body = body;
    }

}
