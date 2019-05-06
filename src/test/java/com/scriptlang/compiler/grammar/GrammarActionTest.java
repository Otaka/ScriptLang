package com.scriptlang.compiler.grammar;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Dmitry
 */
public class GrammarActionTest {
    
    @Test
    public void testProcessEscapement() {
        assertEquals("Hello \"World\"",GrammarAction.processEscapement("Hello `\"World`\""));
        assertEquals("Hello World",GrammarAction.processEscapement("Hello World`"));
    }

}
