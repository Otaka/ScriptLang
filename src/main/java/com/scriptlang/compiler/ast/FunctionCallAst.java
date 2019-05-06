package com.scriptlang.compiler.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class FunctionCallAst extends BaseAst {

    private String functionName;
    private List<BaseAst> arguments = new ArrayList<>();

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setFunctionNameAsToken(TokenAst token) {
        this.functionName = token.getValue();
    }

    public void addArgument(BaseAst argument) {
        arguments.add(argument);
    }

    public List<BaseAst> getArguments() {
        return arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

}
