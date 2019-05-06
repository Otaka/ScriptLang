package com.scriptlang.compiler.astvisitor;

import com.scriptlang.compiler.ast.*;
import com.scriptlang.exceptions.ParseException;

/**
 * @author Dmitry
 */
public class BaseAstVisitor {

    public void visitScriptFileAst(ScriptFileAst scriptAst) {
        for (VariableDeclarationAst var : scriptAst.getGlobalVariables()) {
            visitGlobalVariable(var);
        }

        for (NamedFunctionAst function : scriptAst.getFunctions()) {
            visitNamedFunction(function);
        }
    }

    public void visitNamedFunction(NamedFunctionAst functionAst) {
        visitExpressionList(functionAst.getExpressions());
    }

    public void visitExpressionList(ExpressionListAst expressionListAst) {
        for (BaseAst baseAst : expressionListAst.getExpressions()) {
            visitExpression(baseAst, expressionListAst);
        }
    }

    public void visitDebugLog(DebugLogAst debugLogAst) {
        visitExpression(debugLogAst.getExpression(), debugLogAst);
        visitExpression(debugLogAst.getKey(), debugLogAst);
    }

    public void visitGlobalVariable(VariableDeclarationAst variableDeclarationAst) {
        visitExpression(variableDeclarationAst.getExpression(), variableDeclarationAst);
    }

    public void visitLocalVariableDeclarationAst(VariableDeclarationAst variableDeclarationAst) {
        visitExpression(variableDeclarationAst.getExpression(), variableDeclarationAst);
    }

    public void visitExpression(BaseAst baseAst, BaseAst caller) {
        if (baseAst instanceof LongValueAst) {
            visitLongAst((LongValueAst) baseAst);
        } else if (baseAst instanceof BooleanValueAst) {
            visitBooleanAst((BooleanValueAst) baseAst);
        } else if (baseAst instanceof DoubleValueAst) {
            visitDoubleAst((DoubleValueAst) baseAst);
        } else if (baseAst instanceof StringAst) {
            visitStringAst((StringAst) baseAst);
        } else if (baseAst instanceof VariableUsageAst) {
            visitVariableUsageAst((VariableUsageAst) baseAst);
        } else if (baseAst instanceof DebugLogAst) {
            visitDebugLog((DebugLogAst) baseAst);
        } else if (baseAst instanceof VariableDeclarationAst) {
            visitLocalVariableDeclarationAst((VariableDeclarationAst) baseAst);
        } else if (baseAst instanceof FunctionCallAst) {
            visitFunctionCallAst((FunctionCallAst) baseAst, caller);
        } else if (baseAst instanceof ReturnAst) {
            visitReturnAst((ReturnAst) baseAst);
        } else if (baseAst instanceof BinaryExpressionAst) {
            visitBinaryExpressionAst((BinaryExpressionAst) baseAst);
        } else if (baseAst instanceof NotAst) {
            visitNotAst((NotAst) baseAst);
        } else if (baseAst instanceof IfAst) {
            visitIfAst((IfAst) baseAst);
        } else if (baseAst instanceof ForAst) {
            visitForAst((ForAst) baseAst);
        } else if (baseAst instanceof AssignAst) {
            visitAssignAst((AssignAst) baseAst);
        } else if (baseAst instanceof CreateTableAst) {
            visitCreateTableAst((CreateTableAst) baseAst);
        } else if (baseAst instanceof ExtractFieldAst) {
            visitExtractFieldAst((ExtractFieldAst) baseAst);
        } else {
            throw new RuntimeException("Expression ast [" + baseAst.getClass().getName() + "] is not implemented");
        }
    }

    public void visitCreateTableAst(CreateTableAst ast) {

    }

    public void visitAssignAst(AssignAst assignAst) {
        if (assignAst.getVariable() instanceof TokenAst) {
            visitAssignToVariableAst((TokenAst) assignAst.getVariable(), assignAst);
        } else if (assignAst.getVariable() instanceof ExtractFieldAst) {
            visitAssignToExtractedFieldAst(assignAst, (ExtractFieldAst) assignAst.getVariable());
        }
    }

    public void visitAssignToExtractedFieldAst(AssignAst assignAst, ExtractFieldAst extractField) {
        visitOwnedObjectOfExtractFieldAst(extractField);
        visitExpression(extractField.getKeyExpression(), extractField);
        visitExpression(assignAst.getExpression(), assignAst);
    }

    public void visitExtractFieldAst(ExtractFieldAst extractField) {
        visitOwnedObjectOfExtractFieldAst(extractField);
        visitExpression(extractField.getKeyExpression(), extractField);
    }

    public void visitOwnedObjectOfExtractFieldAst(ExtractFieldAst extractField) {
        if (extractField.getOwnerObject() instanceof ExtractFieldAst) {
            visitExtractFieldAst((ExtractFieldAst) extractField.getOwnerObject());
        } else if (extractField.getOwnerObject() instanceof TokenAst) {
            TokenAst varName=(TokenAst) extractField.getOwnerObject();
            VariableUsageAst varUsageAst=new VariableUsageAst();
            varUsageAst.setStartPosition(varName.getStartPosition());
            varUsageAst.setEndPosition(varName.getEndPosition());
            varUsageAst.setVariableName(varName.getValue());
            visitVariableUsageAst(varUsageAst);
        } else if(extractField.getOwnerObject() instanceof VariableUsageAst){
            visitVariableUsageAst((VariableUsageAst) extractField.getOwnerObject());
        }else {
            throw new ParseException(extractField.getStartPosition(), "Extraction field from ast [" + extractField.getOwnerObject().getClass().getSimpleName() + "] is not implemented");
        }
    }

    public void visitAssignToVariableAst(TokenAst variable, AssignAst assignAst) {
        visitExpression(assignAst.getExpression(), assignAst);
    }

    public void visitNotAst(NotAst notAst) {
        visitExpression(notAst.getExpression(), notAst);
    }

    public void visitForAst(ForAst forAst) {
        visitExpression(forAst.getInitializationExpression(), forAst);
        visitExpression(forAst.getConditionExpression(), forAst);
        visitExpression(forAst.getIncrementExpression(), forAst);
        visitExpressionList(forAst.getBody());
    }

    public void visitIfAst(IfAst ifAst) {
        visitExpression(ifAst.getConditionAst(), ifAst);
        visitExpression(ifAst.getBodyAst(), ifAst);

        if (ifAst.getElseAst() != null) {
            if (ifAst.getElseAst() instanceof ExpressionListAst) {
                visitExpressionList((ExpressionListAst) ifAst.getElseAst());
            } else {
                visitExpression(ifAst.getElseAst(), ifAst);
            }
        }
    }

    public void visitBinaryExpressionAst(BinaryExpressionAst binaryExpressionAst) {
        visitExpression(binaryExpressionAst.getLeft(), binaryExpressionAst);
        visitExpression(binaryExpressionAst.getRight(), binaryExpressionAst);
    }

    public void visitReturnAst(ReturnAst returnAst) {
        int index = -1;

        for (BaseAst expression : returnAst.getExpressions()) {
            index++;
            visitReturnExpression(expression, returnAst, index);
        }
    }

    public void visitReturnExpression(BaseAst expression, ReturnAst returnAst, int index) {
        visitExpression(expression, returnAst);
    }

    public void visitFunctionCallAst(FunctionCallAst functionCallAst, BaseAst caller) {
        //for (int i = 0; i < functionCallAst.getArguments().size(); i++) {
        for (int i = functionCallAst.getArguments().size() - 1; i >= 0; i--) {
            visitExpression(functionCallAst.getArguments().get(i), caller);
        }
    }

    public void visitVariableUsageAst(VariableUsageAst variableAst) {

    }

    public void visitStringAst(StringAst longValueAst) {

    }

    public void visitLongAst(LongValueAst longValueAst) {

    }

    public void visitBooleanAst(BooleanValueAst booleanValueAst) {

    }

    public void visitDoubleAst(DoubleValueAst doubleValueAst) {

    }
}
