package com.scriptlang.compiler.astvisitor;

import com.scriptlang.bytecode.*;
import com.scriptlang.compiler.BytecodeGenerationContext;
import com.scriptlang.compiler.InstructionsContainer;
import com.scriptlang.compiler.Variable;
import com.scriptlang.compiler.VariablesScope;
import com.scriptlang.compiler.ast.*;
import com.scriptlang.exceptions.ParseException;
import com.scriptlang.ffi.ExternalFunctionManager;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Dmitry
 */
public class CompilationToBytecodeVisitor extends BaseAstVisitor {

    private BytecodeGenerationContext context;
    private InstructionsContainer instructionsContainer;
    private NamedFunctionAst currentFunction;
    private int currentLocalVariableIndex;
    private int labelIndex = 1;
    private Map<String, Integer> label2IndexMap = new HashMap<>();
    private Map<String, Map.Entry<Integer, AbstractRelativeJumpInstruction>> label2AbstractRelJumpInstruction = new LinkedHashMap<>();

    public CompilationToBytecodeVisitor(BytecodeGenerationContext context) {
        this.context = context;
    }

    @Override
    public void visitScriptFileAst(ScriptFileAst scriptAst) {
        super.visitScriptFileAst(scriptAst);
        postProcessLabels();
    }

    private void postProcessLabels() {
        for (String label : label2AbstractRelJumpInstruction.keySet()) {
            Map.Entry<Integer, AbstractRelativeJumpInstruction> entry = label2AbstractRelJumpInstruction.get(label);
            AbstractRelativeJumpInstruction jumpInstruction = entry.getValue();
            int instructionPosition = entry.getKey();
            int labelPosition = label2IndexMap.get(label);
            jumpInstruction.setOffset(labelPosition - instructionPosition);
        }

        label2IndexMap.clear();
        label2AbstractRelJumpInstruction.clear();
    }

    private String createNewLabel(String label) {
        String lab = label + "_" + (labelIndex++);
        label2IndexMap.put(lab, instructionsContainer.instructionsCount());
        return lab;
    }

    private void updateLabelPosition(String label) {
        label2IndexMap.put(label, instructionsContainer.instructionsCount());
    }

    private void addInstruction(BaseInstruction baseInstruction) {
        instructionsContainer.addInstruction(baseInstruction);
    }

    private void addRelJumpInstruction(AbstractRelativeJumpInstruction baseInstruction, String label) {
        int index = instructionsContainer.instructionsCount();
        instructionsContainer.addInstruction(baseInstruction);
        Map.Entry<Integer, AbstractRelativeJumpInstruction> entry = new AbstractMap.SimpleImmutableEntry<>(index, baseInstruction);
        label2AbstractRelJumpInstruction.put(label, entry);
    }

    @Override
    public void visitAssignToVariableAst(TokenAst variable, AssignAst assignAst) {
        super.visitAssignToVariableAst(variable, assignAst);
        TokenAst variableToken = (TokenAst) assignAst.getVariable();
        Variable var = context.searchVariable(variableToken.getValue());
        if (var == null) {
            throw new ParseException(assignAst.getStartPosition(), "Variable [" + assignAst.getVariable() + "] is not defined");
        }
        switch (var.getVariableType()) {
            case ARG:
                addInstruction(new SetArgumentVariableInstruction(var.getIndex()));
                break;
            case LOCAL:
                addInstruction(new SetLocalVariableInstruction(var.getIndex()));
                break;
            case GLOBAL:
                addInstruction(new SetGlobalVariableInstruction(var.getIndex()));
                break;
            default:
                throw new IllegalStateException("Variable type [" + var.getVariableType() + "] is not implemented");
        }
    }

    @Override
    public void visitExtractFieldAst(ExtractFieldAst extractField) {
        super.visitExtractFieldAst(extractField);
        addInstruction(new GetFieldFromTableInstruction());
    }

    @Override
    public void visitAssignToExtractedFieldAst(AssignAst assignAst, ExtractFieldAst extractField) {
        super.visitAssignToExtractedFieldAst(assignAst, extractField);
        addInstruction(new SetFieldToTableInstruction());
    }

    @Override
    public void visitCreateTableAst(CreateTableAst ast) {
        addInstruction(new LoadTableInstruction());
    }

    @Override
    public void visitNamedFunction(NamedFunctionAst functionAst) {
        currentFunction = functionAst;
        instructionsContainer = currentFunction;
        currentLocalVariableIndex = 0;
        VariablesScope scope = context.getCurrentScope();
        context.addNewScope();
        addFunctionArgumentsToVariableScope(functionAst);
        super.visitNamedFunction(functionAst);
        context.removeScope();
        if (scope != context.getCurrentScope()) {
            throw new IllegalStateException("Scopes in function [" + functionAst.getFunctionName() + "] not equal. Something inside does not clean scope properly");
        }

        //add instruction that reserve place on stack for local variables
        currentFunction.setLocalVariablesCount(currentLocalVariableIndex);
        if (currentFunction.getLocalVariablesCount() > 0) {
            currentFunction.getCompiledInstructions().add(0, new AddToStackInstruction(currentFunction.getLocalVariablesCount()).setComment("Reserve space for return values").setComment("Reserve space for local variables"));
        }

        postProcessReturnInstructions(functionAst);
        currentLocalVariableIndex = 0;
        currentFunction = null;
        instructionsContainer = null;
    }

    private void postProcessReturnInstructions(NamedFunctionAst function) {
        //add return instruction at the end if it was not added yet(because for example void functions may not have return statement)
        if (!function.getCompiledInstructions().isEmpty()) {
            BaseInstruction lastInstruction = function.getCompiledInstructions().get(function.getCompiledInstructions().size() - 1);
            if (!(lastInstruction instanceof ReturnInstruction)) {
                function.addInstruction(new ReturnInstruction(function.getLocalVariablesCount()));
            }
        } else {
            function.addInstruction(new ReturnInstruction(function.getLocalVariablesCount()));
        }

        //update return instructions to have proper local variable count
        for (BaseInstruction instruction : function.getCompiledInstructions()) {
            if (instruction instanceof ReturnInstruction) {
                ReturnInstruction returnInstruction = (ReturnInstruction) instruction;
                returnInstruction.setLocalVarToRemove(function.getLocalVariablesCount());
            }
        }
    }

    private void addFunctionArgumentsToVariableScope(NamedFunctionAst functionAst) {
        for (int i = 0; i < functionAst.getArgs().size(); i++) {
            String argumentName = functionAst.getArgs().get(i);
            Variable var = new Variable(argumentName, Variable.VariableType.ARG, i, context.getCurrentScope());
            context.addVariable(var);
        }
    }

    @Override
    public void visitBooleanAst(BooleanValueAst booleanValueAst) {
        super.visitBooleanAst(booleanValueAst);
        addInstruction(new LoadBooleanInstruction(booleanValueAst.isValue()));
    }

    @Override
    public void visitDoubleAst(DoubleValueAst doubleValueAst) {
        super.visitDoubleAst(doubleValueAst);
        addInstruction(new LoadDoubleInstruction(doubleValueAst.getValue()));
    }

    @Override
    public void visitLongAst(LongValueAst longValueAst) {
        super.visitLongAst(longValueAst);
        addInstruction(new LoadLongInstruction(longValueAst.getValue()));
    }

    @Override
    public void visitStringAst(StringAst stringValueAst) {
        super.visitStringAst(stringValueAst);
        int constantPoolIndex = context.addStringToPoolIfNotExists(stringValueAst.getValue());
        addInstruction(new LoadStringInstruction(constantPoolIndex));
    }

    @Override
    public void visitVariableUsageAst(VariableUsageAst variableAst) {
        super.visitVariableUsageAst(variableAst);
        String variableName = variableAst.getVariableName();
        Variable variable = context.searchVariable(variableName);
        if (variable == null) {
            throw new ParseException(variableAst.getStartPosition(), "Variable [" + variableName + "] is not defined");
        }

        BaseInstruction instruction;
        switch (variable.getVariableType()) {
            case ARG:
                instruction = new LoadArgumentInstruction(variable.getIndex());
                break;
            case GLOBAL:
                instruction = new LoadGlobalVarInstruction(variable.getIndex());
                break;
            case LOCAL:
                instruction = new LoadLocalVarInstruction(variable.getIndex());
                break;
            default:
                throw new IllegalArgumentException("Variable type [" + variable.getVariableType() + "] is not implemented");
        }

        instruction.setComment("Variable usage " + variableName);
        addInstruction(instruction);
    }

    @Override
    public void visitDebugLog(DebugLogAst debugLogAst) {
        super.visitDebugLog(debugLogAst);
        addInstruction(new DebugLogInstruction());
    }

    @Override
    public void visitFunctionCallAst(FunctionCallAst functionCallAst, BaseAst caller) {
        int callerExpectsReturnVariablesCount = getExpectedReturnValuesCount(caller);
        //allocate data for return values
        if (callerExpectsReturnVariablesCount > 0) {
            addInstruction(new AddToStackInstruction(callerExpectsReturnVariablesCount).setComment("Function call reserve return data space"));
        }

        //if it is local function
        NamedFunctionAst functionDeclaration = context.searchFunction(functionCallAst.getFunctionName());
        if (functionDeclaration != null) {
            if (functionCallAst.getArguments().size() != functionDeclaration.getArgs().size()) {
                int argCount = functionCallAst.getArguments().size();
                throw new ParseException(functionCallAst.getStartPosition(), "You calling function " + functionDeclaration + " but provide only " + argCount + " argument" + (argCount == 1 ? "s" : ""));
            }

            //process arguments
            super.visitFunctionCallAst(functionCallAst, caller);

            //add temp call instruction, later it will be replaced with actual function call instruction
            TempLocalFunctionCallInstruction tempCallInstruction = new TempLocalFunctionCallInstruction(functionDeclaration, callerExpectsReturnVariablesCount);
            addInstruction(tempCallInstruction);
            return;
        }

        ExternalFunctionManager.ExternalFunctionData externalFunctionData = context.getExternalFunctionManager().getExternalFunction(functionCallAst.getFunctionName());
        if (externalFunctionData != null) {
            if (callerExpectsReturnVariablesCount > 1) {
                throw new ParseException(functionCallAst.getStartPosition(), "External functions can return maximum one value, but you expects " + callerExpectsReturnVariablesCount + " while calling [" + functionCallAst.getFunctionName() + "]");
            }

            if (functionCallAst.getArguments().size() != externalFunctionData.getArgumentsCount()) {
                throw new ParseException(functionCallAst.getStartPosition(), "You want to call external function [" + functionCallAst.getFunctionName() + "] that accepts [" + externalFunctionData.getArgumentsCount() + "] but you have provided [" + functionCallAst.getArguments().size() + "]");
            }

            super.visitFunctionCallAst(functionCallAst, caller);
            addInstruction(new ExternalFunctionCallInstruction(functionCallAst.getFunctionName(), callerExpectsReturnVariablesCount, externalFunctionData));
            return;
        }

        throw new ParseException(functionCallAst.getStartPosition(), "Function [" + functionCallAst.getFunctionName() + "] is not defined");
    }

    private int getExpectedReturnValuesCount(BaseAst caller) {
        int callerExpectsReturnVariablesCount = 1;
        if (caller instanceof ExpressionListAst) {
            callerExpectsReturnVariablesCount = 0;
        } else if (caller instanceof VariableDeclarationAst) {
            VariableDeclarationAst varDeclaration = (VariableDeclarationAst) caller;
            callerExpectsReturnVariablesCount = varDeclaration.getVarNames().size();
        }

        return callerExpectsReturnVariablesCount;
    }

    @Override
    public void visitNotAst(NotAst notAst) {
        super.visitNotAst(notAst);
        addInstruction(new NotInstruction());
    }

    @Override
    public void visitForAst(ForAst forAst) {
        String loopIterationLabel = createNewLabel("for_iteration");
        String loopFinishLabel = createNewLabel("for_finish");

        context.addNewScope();
        visitExpression(forAst.getInitializationExpression(), forAst);
        updateLabelPosition(loopIterationLabel);
        visitExpression(forAst.getConditionExpression(), forAst);
        addRelJumpInstruction(new JumpFalseInstruction(-1), loopFinishLabel);

        visitExpressionList(forAst.getBody());

        visitExpression(forAst.getIncrementExpression(), forAst);
        addRelJumpInstruction(new JumpRelInstruction(-1), loopIterationLabel);
        updateLabelPosition(loopFinishLabel);
        context.removeScope();
    }

    @Override
    public void visitIfAst(IfAst ifAst) {
        visitExpression(ifAst.getConditionAst(), ifAst);
        if (ifAst.getElseAst() == null) {
            String ifEndLabel = createNewLabel("if_end");
            JumpFalseInstruction jumpInstruction = new JumpFalseInstruction(-1);
            addRelJumpInstruction(jumpInstruction, ifEndLabel);
            visitExpressionList(ifAst.getBodyAst());
            updateLabelPosition(ifEndLabel);
        } else {
            String elseStartBlock = createNewLabel("else_start_block");
            String ifFinishBlock = createNewLabel("if_finish_block");

            addRelJumpInstruction(new JumpFalseInstruction(-1), elseStartBlock);
            visitExpressionList(ifAst.getBodyAst());
            addRelJumpInstruction(new JumpRelInstruction(-1), ifFinishBlock);

            updateLabelPosition(elseStartBlock);
            if (ifAst.getElseAst() instanceof ExpressionListAst) {
                visitExpressionList((ExpressionListAst) ifAst.getElseAst());
            } else {
                visitExpression(ifAst.getElseAst(), ifAst);
            }

            updateLabelPosition(ifFinishBlock);
        }
    }

    @Override
    public void visitBinaryExpressionAst(BinaryExpressionAst binaryExpressionAst) {
        super.visitBinaryExpressionAst(binaryExpressionAst);
        switch (binaryExpressionAst.getOperation()) {
            case "+": {
                addInstruction(new SumInstruction());
                break;
            }
            case "-": {
                addInstruction(new SubInstruction());
                break;
            }
            case "*": {
                addInstruction(new MulInstruction());
                break;
            }
            case "/": {
                addInstruction(new DivInstruction());
                break;
            }
            case "%": {
                addInstruction(new ModInstruction());
                break;
            }
            case "&": {
                addInstruction(new BitAndInstruction());
                break;
            }
            case "|": {
                addInstruction(new BitOrInstruction());
                break;
            }
            case "^": {
                addInstruction(new BitXorInstruction());
                break;
            }
            case "==": {
                addInstruction(new EqualsInstruction());
                break;
            }
            case "!=": {
                addInstruction(new NotEqualsInstruction());
                break;
            }
            case "===": {
                addInstruction(new StrictEqualsInstruction());
                break;
            }
            case "!==": {
                addInstruction(new StrictNotEqualsInstruction());
                break;
            }
            case "<": {
                addInstruction(new LessThanInstruction());
                break;
            }
            case ">": {
                addInstruction(new MoreThanInstruction());
                break;
            }
            case "<=": {
                addInstruction(new LessOrEqualInstruction());
                break;
            }
            case ">=": {
                addInstruction(new MoreOrEqualInstruction());
                break;
            }
            default:
                throw new IllegalStateException("Binary operation [" + binaryExpressionAst.getOperation() + "] is not implemented");
        }
    }

    @Override
    public void visitReturnAst(ReturnAst returnAst) {
        super.visitReturnAst(returnAst);
        ReturnInstruction returnInstruction = new ReturnInstruction(currentFunction.getLocalVariablesCount());
        addInstruction(returnInstruction);
    }

    @Override
    public void visitReturnExpression(BaseAst expression, ReturnAst returnAst, int index) {
        super.visitReturnExpression(expression, returnAst, index);
        addInstruction(new SetReturnVarInstruction(index));
    }

    @Override
    public void visitExpressionList(ExpressionListAst expressionListAst) {
        context.addNewScope();
        super.visitExpressionList(expressionListAst);
        context.removeScope();
    }

    @Override
    public void visitLocalVariableDeclarationAst(VariableDeclarationAst variableDeclarationAst) {
        //for (int i=variableDeclarationAst.getVarNames().size()-1;i>=0;i--) {
        for (int i = 0; i < variableDeclarationAst.getVarNames().size(); i++) {
            String varName = variableDeclarationAst.getVarNames().get(i);
            Variable variable = context.searchVariable(varName);
            if (variable != null && (variable.getScope() == context.getCurrentScope())) {
                throw new ParseException(variableDeclarationAst.getStartPosition(), "Variable [" + varName + "] already defined in this scope");
            }

            variable = new Variable(varName, Variable.VariableType.LOCAL, currentLocalVariableIndex, context.getCurrentScope());
            currentLocalVariableIndex++;
            context.addVariable(variable);
        }

        //In case if initialization expression is function - function can return several values at once, that is why single execution initializes all variables at once. If function provides not enough values, all other will be null
        //in case if initialization expression is not function - it will be repeated for all variables one by one
        if (variableDeclarationAst.getExpression() instanceof FunctionCallAst) {
            super.visitLocalVariableDeclarationAst(variableDeclarationAst);
            // for (String varName : variableDeclarationAst.getVarNames()) {
            for (int i = variableDeclarationAst.getVarNames().size() - 1; i >= 0; i--) {
                String varName = variableDeclarationAst.getVarNames().get(i);
                Variable var = context.searchVariable(varName);
                addInstruction(new SetLocalVariableInstruction(var.getIndex()).setComment("Set variable " + var.getName()));
            }
        } else {
            for (String varName : variableDeclarationAst.getVarNames()) {
                super.visitLocalVariableDeclarationAst(variableDeclarationAst);
                Variable var = context.searchVariable(varName);
                addInstruction(new SetLocalVariableInstruction(var.getIndex()).setComment("Set variable " + var.getName()));
            }
        }
    }
}
