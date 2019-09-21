package com.scriptlang.compiler.grammar;

import com.scriptlang.compiler.ast.*;
import static org.parboiled.BaseParser.ANY;
import static org.parboiled.BaseParser.EOI;
import org.parboiled.Rule;
import org.parboiled.annotations.SuppressNode;

/**
 * @author Dmitry
 */
public class Grammar extends GrammarAction {

    public Rule start() {
        return Sequence(pushNewObject(ScriptFileAst.class),
                whitespace(),
                topLevelStatements(),
                whitespace(),
                FirstOf(EOI, actionFail("Expected end of file, but found text")));
    }

    Rule topLevelStatements() {
        return FirstOf(
                OneOrMore(
                        TestNot(EOI), whitespace(), topLevelStatement(), whitespace()
                ),
                actionFail("Source file should have at least one statement")
        );
    }

    Rule topLevelStatement() {
        return FirstOf(
                Sequence(
                        globalVarDeclarationExpression(),
                        semicolonOrFail(),
                        popTopToNextTop(ScriptFileAst.class, "addGlobalVar", VariableDeclarationAst.class, "globalVar")
                ),
                Sequence(namedFunctionDeclaration(),
                        popTopToNextTop(ScriptFileAst.class, "addFunction", NamedFunctionAst.class, "AddNamedFunction")
                ),
                actionFail("Expected global variable or named function")
        );
    }

    Rule namedFunctionDeclaration() {
        return Sequence(keyword("f"),
                pushNewObject(NamedFunctionAst.class),
                ensureWhitespaceThrowError(),
                FirstOf(
                        token(),
                        actionFail("Expected function name after 'f'")
                ),
                setTopTokenToNextTop(NamedFunctionAst.class, "setFunctionName"),
                FirstOf(
                        keyword("("),
                        actionFail("Expected '(arguments)' after function name")
                ),
                Optional(token(),
                        setTopTokenToNextTop(NamedFunctionAst.class, "addArg"),
                        ZeroOrMore(
                                keyword(","),
                                token(),
                                setTopTokenToNextTop(NamedFunctionAst.class, "addArg")
                        )
                ),
                FirstOf(
                        keyword(")"),
                        actionFail("Expected closing bracket for function arguments declaration")
                ),
                functionCodeBlock()
        );
    }

    Rule functionCodeBlock() {
        return Sequence(
                FirstOf(
                        keyword("{"),
                        actionFail("Expected function body '{'...'}'")
                ),
                expressionsList(),
                popTopToNextTop(NamedFunctionAst.class, "setExpressions", ExpressionListAst.class, "extract expressions for function code block"),
                FirstOf(
                        keyword("}"),
                        actionFail("Expected function body end '}'")
                )
        );
    }

    Rule expressionsList() {
        return Sequence(
                pushNewObject(ExpressionListAst.class),
                ZeroOrMore(
                        Sequence(
                                inFunctionStatement(),
                                popTopToNextTop(ExpressionListAst.class, "addExpression", BaseAst.class, "Extract expression for expression list")
                        )
                )
        );
    }

    Rule inFunctionStatement() {
        return FirstOf(
                ifStatement(),
                forStatement(),
                Sequence(varDeclarationExpression(), semicolonOrFail()),
                Sequence(debugLogObjectExpression(), semicolonOrFail()),
                returnStatement(),
                Sequence(assignValue(), semicolonOrFail()),
                Sequence(expression(), semicolonOrFail())
        );
    }

    Rule forStatement() {
        return Sequence(
                keyword("for"),
                pushNewObject(ForAst.class),
                FirstOf(keyword("("), actionFail("Expected '(' after 'for'")),
                forInitializationExpression(),
                popTopToNextTop(ForAst.class, "setInitializationExpression", BaseAst.class, "'For' initialization expression"),
                FirstOf(keyword(";"), actionFail("Expected ';' after 'for' initialization expression")),
                expression(),
                popTopToNextTop(ForAst.class, "setConditionExpression", BaseAst.class, "'For' condition expression"),
                FirstOf(keyword(";"), actionFail("Expected ';' after 'for' condition expression")),
                assignValue(),
                popTopToNextTop(ForAst.class, "setIncrementExpression", BaseAst.class, "'For' increment expression"),
                FirstOf(keyword(")"), actionFail("Expected closing ')' after 'for(...' ")),
                FirstOf(keyword("{"), actionFail("Expected '{' for expressions list for 'for' loop")),
                expressionsList(),
                popTopToNextTop(ForAst.class, "setBody", ExpressionListAst.class, "'For' body expressions list"),
                FirstOf(keyword("}"), actionFail("Expected '}' for expressions list for 'for' loop"))
        );
    }

    Rule forInitializationExpression() {
        return FirstOf(varDeclarationExpression(), actionFail("Expected 'for' initialization expression(var X=...)"));
    }

    Rule ifStatement() {
        return Sequence(
                keyword("if"),
                pushNewObject(IfAst.class),
                FirstOf(keyword("("), actionFail("Expected condition in (...) after if")),
                FirstOf(expression(), actionFail("Expected condition in (...) after if")),
                popTopToNextTop(IfAst.class, "setConditionAst", BaseAst.class, "set condition expression for if"),
                FirstOf(keyword(")"), actionFail("Expected condition in (...) after if")),
                FirstOf(keyword("{"), actionFail("Expected if body in {...} after if(condition)")),
                expressionsList(),
                popTopToNextTop(IfAst.class, "setBodyAst", ExpressionListAst.class, "set body expression for if"),
                FirstOf(keyword("}"), actionFail("Expected closing bracket '}' after if body")),
                Optional(
                        keyword("else"),
                        FirstOf(
                                Sequence(ensureWhitespace(), ifStatement(), popTopToNextTop(IfAst.class, "setElseAst", IfAst.class, "set 'else if' for if")),
                                Sequence(
                                        keyword("{"),
                                        expressionsList(),
                                        popTopToNextTop(IfAst.class, "setElseAst", BaseAst.class, "set else expressions for if"),
                                        FirstOf(keyword("}"), actionFail("Expected end of else block '}'"))
                                ),
                                actionFail("Else block should be another if, or expressions block {...}")
                        )
                )
        );
    }

    Rule returnStatement() {
        return Sequence(
                keyword("return"),
                pushNewObject(ReturnAst.class),
                FirstOf(
                        semicolon(),
                        Sequence(
                                ensureWhitespaceThrowError(),
                                expression(),
                                popTopToNextTop(ReturnAst.class, "addReturnExpression", BaseAst.class, "Add return expression"),
                                ZeroOrMore(
                                        keyword(","),
                                        expression(),
                                        popTopToNextTop(ReturnAst.class, "addReturnExpression", BaseAst.class, "Add return expression2+")
                                ),
                                semicolonOrFail()
                        ),
                        actionFail("Expected ';' or {expression} after return statement")
                )
        );
    }

    Rule varDeclarationExpression() {
        return Sequence(
                keyword("var"),
                pushNewObject(VariableDeclarationAst.class),
                ensureWhitespaceThrowError(),
                FirstOf(token(), actionFail("Expected variable name")),
                setTopTokenToNextTop(VariableDeclarationAst.class, "addVarName"),
                ZeroOrMore(
                        keyword(","),
                        FirstOf(
                                token(),
                                actionFail("Expected variable name")
                        ),
                        setTopTokenToNextTop(VariableDeclarationAst.class, "addVarName")
                ),
                FirstOf(keyword("="), actionFail("Expected '= {expression}' after variable declaration")),
                FirstOf(expression(), actionFail("Expected expression to initialize variable")),
                popTopToNextTop(VariableDeclarationAst.class, "setExpression", BaseAst.class, "Variable initialization expression")
        );
    }

    Rule globalVarDeclarationExpression() {
        return Sequence(
                keyword("var"),
                pushNewObject(VariableDeclarationAst.class),
                ensureWhitespaceThrowError(),
                FirstOf(token(), actionFail("Expected variable name")),
                setTopTokenToNextTop(VariableDeclarationAst.class, "addVarName"),
                FirstOf(keyword("="), actionFail("Expected '= {expression}' after global variable declaration")),
                FirstOf(expression(), actionFail("Expected expression to initialize global variable")),
                popTopToNextTop(VariableDeclarationAst.class, "setExpression", BaseAst.class, "Global Variable initialization expression")
        );
    }

    Rule expression() {
        return logicalExpressionRule();
    }

    public Rule assignValue() {
        return Sequence(
                FirstOf(
                        Sequence(
                                token(),
                                OneOrMore(
                                        keyword("["),
                                        pushNewObject(ExtractFieldAst.class),
                                        popSecondObjectToTop(ExtractFieldAst.class, "setOwnerObject", BaseAst.class),
                                        expression(),
                                        popTopToNextTop(ExtractFieldAst.class, "setKeyExpression", BaseAst.class, "expression for table's key ( X[KEY] )"),
                                        keyword("]")
                                )
                        ),
                        token()//to handle just assignment to variable i.e. x=3
                ),
                keyword("="),
                pushNewObject(AssignAst.class),
                popSecondObjectToTop(AssignAst.class, "setVariable", BaseAst.class),
                expression(),
                popTopToNextTop(AssignAst.class, "setExpression", BaseAst.class, "assign expression for 'assign' statement")
        );
    }

    public Rule logicalExpressionRule() {
        return Sequence(bitManipExpressionRule(),
                ZeroOrMore(ensureWhitespace(),
                        FirstOf(
                                keyword("or"),
                                keyword("and")
                        ),
                        push(match().trim()),
                        ensureWhitespace(),
                        FirstOf(
                                bitManipExpressionRule(),
                                actionFail("Expected expression after and/or")
                        ),
                        pushNewObject(BinaryExpressionAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setRight", BaseAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setOperation", String.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setLeft", BaseAst.class)
                )
        );
    }

    public Rule bitManipExpressionRule() {
        return Sequence(equalExpressionRule(),
                ZeroOrMore(
                        FirstOf(
                                keyword("&"),
                                keyword("|"),
                                keyword("^")
                        ),
                        push(match().trim()),
                        FirstOf(
                                equalExpressionRule(),
                                actionFail("& or | or ^ expects second expression")
                        ),
                        pushNewObject(BinaryExpressionAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setRight", BaseAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setOperation", String.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setLeft", BaseAst.class)
                )
        );
    }

    public Rule equalExpressionRule() {
        return Sequence(sumExpressionRule(),
                ZeroOrMore(
                        FirstOf(//this order is very important, we should go from bigest to smalest, to prevent situation when small pattern "eats' part of actual operation. For example '===' can be parsed as '==' and '='
                                keyword("==="), keyword("!=="), keyword("=="), keyword("!="), keyword(">="), keyword("<="), keyword(">"), keyword("<")
                        ),
                        push(match().trim()),
                        FirstOf(
                                sumExpressionRule(),
                                actionFail("Equality operator expects second expression")
                        ),
                        pushNewObject(BinaryExpressionAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setRight", BaseAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setOperation", String.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setLeft", BaseAst.class)
                )
        );
    }

    Rule sumExpressionRule() {
        return Sequence(
                divMulExpressionRule(),
                ZeroOrMore(
                        FirstOf(
                                keyword("+"),
                                keyword("-"),
                                keyword("%")
                        ),
                        push(match().trim()),
                        FirstOf(
                                divMulExpressionRule(),
                                actionFail("Expected second expression for operation +/-/%")
                        ),
                        pushNewObject(BinaryExpressionAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setRight", BaseAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setOperation", String.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setLeft", BaseAst.class)
                )
        );
    }

    Rule divMulExpressionRule() {
        return Sequence(
                atomWrapper(),
                ZeroOrMore(
                        FirstOf(keyword("/"), keyword("*")),
                        push(match().trim()),
                        FirstOf(
                                atomWrapper(),
                                actionFail("Expected second expression for '*' '/'")
                        ),
                        pushNewObject(BinaryExpressionAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setRight", BaseAst.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setOperation", String.class),
                        popSecondObjectToTop(BinaryExpressionAst.class, "setLeft", BaseAst.class)
                )
        );
    }

    Rule atomWrapper() {
        return Sequence(
                atom(),
                ZeroOrMore(
                        keyword("["),
                        pushNewObject(ExtractFieldAst.class),
                        popSecondObjectToTop(ExtractFieldAst.class, "setOwnerObject", BaseAst.class),
                        FirstOf(expression(),actionFail("Expected key for field extraction expression")),
                        popTopToNextTop(ExtractFieldAst.class, "setKeyExpression", BaseAst.class, "key for field extraction"),
                        FirstOf(keyword("]"),actionFail("Expected closing symbol ']' for field extraction expression"))
                )
        );
    }

    Rule atom() {
        return FirstOf(
                notRule(),
                parens(),
                stringRule(),
                booleanValue(),
                hexNumber(),
                floatNumber(),
                integerDecimalNumber(),
                functionCall(),
                variableUsage(),
                initTable()
        );
    }

    Rule initTable() {
        return Sequence(
                keyword("{"),
                pushNewObject(CreateTableAst.class),
                FirstOf(keyword("}"), actionFail("Expected close '}' symbol of table"))
        );
    }

    Rule notRule() {
        return Sequence(
                keyword("!"),
                keyword("("),
                pushNewObject(NotAst.class),
                expression(),
                popTopToNextTop(NotAst.class, "setExpression", BaseAst.class, "Expression for Not statement"),
                keyword(")")
        );
    }

    Rule parens() {
        return Sequence(
                keyword("("),
                expression(),
                keyword(")")
        );
    }

    Rule functionCall() {
        return Sequence(
                token(),
                pushNewObject(FunctionCallAst.class),
                keyword("("),
                popSecondObjectToTop(FunctionCallAst.class, "setFunctionNameAsToken", TokenAst.class),
                Optional(
                        Sequence(
                                Sequence(
                                        TestNot(keyword(")")),
                                        FirstOf(
                                                Sequence(
                                                        expression(),
                                                        popTopToNextTop(FunctionCallAst.class, "addArgument", BaseAst.class, "function call argument")
                                                ),
                                                actionFail("Expected function argument")
                                        )
                                ),
                                ZeroOrMore(
                                        TestNot(keyword(")")),
                                        FirstOf(
                                                Sequence(
                                                        keyword(","),
                                                        TestNot(keyword(")")),
                                                        FirstOf(
                                                                Sequence(
                                                                        expression(),
                                                                        popTopToNextTop(FunctionCallAst.class, "addArgument", BaseAst.class, "function call argument seq")
                                                                ),
                                                                actionFail("Expected function argument")
                                                        )
                                                ),
                                                actionFail("Expected ',' after function argument")
                                        )
                                )
                        )
                ),
                keyword(")")
        );
    }

    Rule debugLogObjectExpression() {
        return Sequence(keyword("debuglog"), pushNewObject(DebugLogAst.class),
                ensureWhitespaceThrowError(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression that returns string for 'key' for 'debuglog'. 'debuglog 'KEY' ${expression}'")
                ),
                popTopToNextTop(DebugLogAst.class, "setKey", BaseAst.class, "Expression for debuglog key"),
                ensureWhitespaceThrowError(),
                FirstOf(
                        expression(),
                        actionFail("Expected expression for 'debuglog'. 'debuglog ${expression}'")
                ),
                popTopToNextTop(DebugLogAst.class, "setExpression", BaseAst.class, "Extract expression for debuglog")
        );
    }

    Rule variableUsage() {
        return Sequence(
                pushNewObject(VariableUsageAst.class),
                token(),
                setTopTokenToNextTop(VariableUsageAst.class, "setVariableName")
        );
    }

    Rule integerDecimalNumber() {
        return Sequence(
                Sequence(
                        whitespace(),
                        Optional("-"),
                        whitespace(),
                        OneOrMore(
                                FirstOf(
                                        CharRange('0', '9'),
                                        "_"
                                )
                        )
                ),
                pushInteger(getContext())
        );
    }

    Rule floatNumber() {
        return Sequence(
                Sequence(whitespace(),
                        Optional("-"),
                        whitespace(),
                        OneOrMore(
                                FirstOf(
                                        CharRange('0', '9'),
                                        "_"
                                )
                        ),
                        ".",
                        OneOrMore(
                                FirstOf(
                                        CharRange('0', '9'),
                                        "_"
                                )
                        )
                ),
                pushFloat(getContext())
        );
    }

    Rule booleanValue() {
        return Sequence(
                FirstOf(keyword("true"), keyword("false")),
                pushBoolean()
        );
    }

    Rule hexNumber() {
        return Sequence(whitespace(), "0x",
                OneOrMore(
                        FirstOf(
                                CharRange('a', 'f'),
                                CharRange('A', 'F'),
                                CharRange('0', '9'),
                                "_"
                        )
                ),
                pushHexInt(getContext())
        );
    }

    Rule binNumber() {
        return Sequence(whitespace(), "0b",
                OneOrMore(
                        FirstOf("0", "1", "_")
                ),
                pushBinInt(getContext())
        );
    }

    Rule stringRule() {
        return Sequence(
                new StringMatcher('`', '"', "Tried to find end of line symbol [$stringBoundary] but end of file was found. You forget to finish line with [$stringBoundary]"),
                pushString(getContext())
        );
    }

    //Rule argumentsDeclaration(){
    //}
    Rule keyword(String keyword) {
        return Sequence(whitespace(), String(keyword));
    }

    Rule token() {
        return Sequence(whitespace(), innerToken(), tokenAction(getContext()));
    }

    @SuppressNode
    Rule innerToken() {
        return Sequence(
                FirstOf(
                        CharRange('a', 'z'),
                        CharRange('A', 'Z'),
                        "_"
                ),
                ZeroOrMore(
                        FirstOf(
                                CharRange('a', 'z'),
                                CharRange('A', 'Z'),
                                CharRange('0', '9'),
                                "_"
                        ))
        );
    }

    @SuppressNode
    Rule whitespace() {
        return ZeroOrMore(
                FirstOf(
                        blank(),
                        comment()
                )
        );
    }

    @SuppressNode
    Rule ensureWhitespace() {
        return OneOrMore(
                FirstOf(
                        blank(),
                        comment()
                )
        );
    }

    @SuppressNode
    Rule ensureWhitespaceThrowError() {
        return FirstOf(
                ensureWhitespace(),
                actionFail("Expected space")
        );
    }

    Rule comment() {
        return FirstOf(
                Sequence(
                        "/*",
                        ZeroOrMore(
                                Sequence(
                                        TestNot("*/"),
                                        ANY
                                )
                        ),
                        "*/"
                ),
                Sequence(
                        "//",
                        ZeroOrMore(
                                Sequence(
                                        TestNot(eol()),
                                        ANY
                                )
                        )
                )
        );
    }

    Rule eol() {
        return FirstOf(
                Sequence("\n", Optional("\r")),
                Sequence("\r", Optional("\n"))
        );
    }

    @SuppressNode
    Rule semicolonOrFail() {
        return Sequence(
                whitespace(),
                FirstOf(
                        String(";"),
                        actionFail("Expected ';'")
                )
        );
    }

    @SuppressNode
    Rule semicolon() {
        return Sequence(
                whitespace(),
                String(";")
        );
    }

    @SuppressNode
    Rule blank() {
        return FirstOf(" ", "\t", "\n", "\r");
    }
}
