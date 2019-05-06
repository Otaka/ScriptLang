package com.scriptlang;

import com.scriptlang.bytecode.BaseInstruction;
import com.scriptlang.bytecode.LocalFunctionCallInstruction;
import com.scriptlang.bytecode.TempLocalFunctionCallInstruction;
import com.scriptlang.compiler.BytecodeGenerationContext;
import com.scriptlang.compiler.Variable;
import com.scriptlang.compiler.ast.NamedFunctionAst;
import com.scriptlang.compiler.ast.ScriptFileAst;
import com.scriptlang.compiler.ast.VariableDeclarationAst;
import com.scriptlang.compiler.astvisitor.CompilationToBytecodeVisitor;
import com.scriptlang.compiler.grammar.Grammar;
import com.scriptlang.exceptions.ParseException;
import com.scriptlang.ffi.ExternalFunctionManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author Dmitry
 */
public class Compiler {

    private ExternalFunctionManager platformFunctions = new ExternalFunctionManager();

    public void setPlatformFunctions(ExternalFunctionManager platformFunctions) {
        this.platformFunctions = platformFunctions;
    }

    public CompiledScriptFile compileFile(String path) throws IOException {
        String content = readFileFromPath(path);
        return compileFile(content, path);
    }

    public CompiledScriptFile compileFile(String content, String fileName) {
        Grammar grammar = Parboiled.createParser(Grammar.class);
        BasicParseRunner parseRunner = new BasicParseRunner(grammar.start());
        try {
            ParsingResult result = parseRunner.run(content);
            if (!result.matched) {
                throw new IllegalArgumentException("Error while parsing source file [" + fileName + "]");
            }

            ScriptFileAst scriptFile = (ScriptFileAst) result.valueStack.pop();
            BytecodeGenerationContext context = new BytecodeGenerationContext(platformFunctions);
            initBytecodeGenerationContext(context, scriptFile);
            CompilationToBytecodeVisitor visitor = new CompilationToBytecodeVisitor(context);
            visitor.visitScriptFileAst(scriptFile);
            CompiledScriptFile compiledScriptFile = createCompiledScriptFile(context, fileName);
            return compiledScriptFile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private CompiledScriptFile createCompiledScriptFile(BytecodeGenerationContext context, String fileName) {
        //put [main] function at top
        NamedFunctionAst mainFunction = context.searchFunction("main");
        if (mainFunction != null) {
            context.getFoundFunctions().remove(mainFunction);
            context.getFoundFunctions().add(0, mainFunction);
        }

        //combine all functions instructions to single instructions list
        List<BaseInstruction> instructions = new ArrayList<>();
        Map<String, Integer> functionName2IP = new HashMap<>();
        for (NamedFunctionAst function : context.getFoundFunctions()) {
            functionName2IP.put(function.getFunctionName(), instructions.size());
            instructions.addAll(function.getCompiledInstructions());
        }

        postProcessTempFunctionCallInstructions(functionName2IP, context, instructions);

        CompiledScriptFile scriptFile = new CompiledScriptFile(
                functionName2IP,
                instructions.toArray(new BaseInstruction[0]),
                context.getStringPool().toArray(new String[0])
        );
        scriptFile.setScriptFileName(fileName);

        return scriptFile;
    }

    private void postProcessTempFunctionCallInstructions(Map<String, Integer> functionName2IP, BytecodeGenerationContext context, List<BaseInstruction> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            BaseInstruction currentInstruction = instructions.get(i);
            if (currentInstruction instanceof TempLocalFunctionCallInstruction) {
                TempLocalFunctionCallInstruction tempCallInstruction = (TempLocalFunctionCallInstruction) currentInstruction;
                int address = functionName2IP.get(tempCallInstruction.getFunctionToCall().getFunctionName());
                int argsCount = tempCallInstruction.getFunctionToCall().getArgs().size();
                LocalFunctionCallInstruction callInstruction = new LocalFunctionCallInstruction(address, argsCount, tempCallInstruction.getWaitingForReturnVariablesCount());
                callInstruction.setComment(tempCallInstruction.toString());
                instructions.set(i, callInstruction);
            }
        }
    }

    private void initBytecodeGenerationContext(BytecodeGenerationContext context, ScriptFileAst scriptFileAst) {
        for (NamedFunctionAst function : scriptFileAst.getFunctions()) {
            if (context.searchFunction(function.getFunctionName()) != null) {
                throw new ParseException(function.getStartPosition(), "Function with name [" + function.getFunctionName() + "] already exists");
            }

            context.getFoundFunctions().add(function);
        }

        int varIndex = -1;
        for (VariableDeclarationAst globalVarDeclaration : scriptFileAst.getGlobalVariables()) {
            String varName = globalVarDeclaration.getVarNames().get(0);
            if (context.searchVariable(varName) != null) {
                throw new ParseException(globalVarDeclaration.getStartPosition(), "Global variable with name [" + varName + " already declared]");
            }

            varIndex++;
            Variable globalVar = new Variable(varName, Variable.VariableType.GLOBAL, varIndex, context.getCurrentScope());
            context.addVariable(globalVar);
        }

        if (!scriptFileAst.getGlobalVariables().isEmpty()) {
            context.addNewScope();
        }
    }

    public String readFileFromPath(String path) throws IOException {
        String content;
        if (path.startsWith("resource://")) {
            path = path.substring("resource://".length());
            try (InputStream stream = Interpreter.class.getResourceAsStream(path)) {
                if (stream == null) {
                    throw new IllegalArgumentException("Cannot find path in resources [" + path + "]");
                }
                content = readContent(stream);
            }
        } else {
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("Cannot find file [" + path + "]");
            }
            try (InputStream stream = new FileInputStream(file)) {
                content = readContent(stream);
            }
        }
        return content;
    }

    private String readContent(InputStream stream) {
        Scanner scanner = new Scanner(stream, "UTF-8");
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;
        while (scanner.hasNextLine()) {
            if (firstLine == false) {
                sb.append("\n");
            }
            sb.append(scanner.nextLine());
            firstLine = false;
        }

        return sb.toString();
    }
}
