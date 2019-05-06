package com.scriptlang.compiler.grammar;

import com.scriptlang.exceptions.ParseException;
import org.parboiled.MatcherContext;
import org.parboiled.matchers.CustomMatcher;

/**
 * @author Dmitry<br>
 * matcher does not do escape symbols processing, because matcher
 * cannot modify the input(i.e. cannot replace symbols '\' 'n' with '\n')
 */
public class StringMatcher extends CustomMatcher {

    private char escapeSymbol = '`';
    private char stringBoundary = '\"';
    private String errorMessage = "Error while searched end of string, but reached end(you forget to close the string)";

    public StringMatcher() {
        super("StringMatcher");
    }

    public StringMatcher(char escapeSymbol, char stringBoundary, String errorMessage) {
        super("StringMatcher");
        this.escapeSymbol = escapeSymbol;
        this.stringBoundary = stringBoundary;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isSingleCharMatcher() {
        return false;
    }

    @Override
    public boolean canMatchEmpty() {
        return false;
    }

    @Override
    public boolean isStarterChar(char c) {
        return false;
    }

    @Override
    public char getStarterChar() {
        return (char) -1;
    }

    private String getErrorMessage() {
        return errorMessage.replace("$escapeSymbol", "" + escapeSymbol).replace("$stringBoundary", "" + stringBoundary);
    }

    @Override
    public <V> boolean match(MatcherContext<V> context) {
        int startIndex = context.getCurrentIndex();
        char c = context.getCurrentChar();
        if (!(c == '`' || c == '"')) {
            return false;
        }

        Object valueStackSnapshot = context.getValueStack().takeSnapshot();
        if (c == '`') {//advanced string
            context.advanceIndex(1);
            char tStringBoundary = context.getCurrentChar();
            context.advanceIndex(1);
            char oldChar = (char) -1;
            while (true) {
                c = context.getCurrentChar();
                context.advanceIndex(1);
                if (c == (char) -1) {
                    throw new ParseException(startIndex, getErrorMessage());
                }

                if (c == tStringBoundary && oldChar != escapeSymbol) {
                    context.createNode();
                    return true;
                }

                oldChar = c;
            }
        } else if (c == stringBoundary) {
            context.advanceIndex(1);
            char oldChar = (char) -1;
            while (true) {
                c = context.getCurrentChar();
                context.advanceIndex(1);
             
                if (c == (char) -1) {
                    throw new ParseException(startIndex, getErrorMessage());
                }

                if (c == stringBoundary && oldChar != escapeSymbol) {
                    context.createNode();
                    return true;
                }

                oldChar = c;
            }
        } else {
            context.getValueStack().restoreSnapshot(valueStackSnapshot);
            return false;
        }
    }
}
