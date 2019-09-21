package com.scriptlang;

import com.scriptlang.data.MutableVar;
import com.scriptlang.ffi.ExternalFunction;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class InterpreterTest extends BaseTest {

    private static void printDebugLogKeys() {
        DebugLog.getKeyValueMap();
    }

    @Test
    public void testCallInfo() {
        int callInfo = Interpreter.createCallInfo((short) 5, (short) 6, (short) 2);
        Assert.assertEquals(5, Interpreter.getExpectedArgsCountFromCallInfo(callInfo));
        Assert.assertEquals(6, Interpreter.getProvidedArgsCountFromCallInfo(callInfo));
        Assert.assertEquals(2, Interpreter.getReturnValuesFromCallInfo(callInfo));
    }

    @Test
    public void testSimpleTest() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/00_simplest_test.script");
        Assert.assertEquals(new MutableVar(89), getAndRemoveDebugLogCheckExists("output"));

        checkNothingLeftInDebugLog();
    }

    @Test
    public void testStrings() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/01_stringsTest.script");
        Assert.assertEquals("", getAndRemoveStringFromDebugLogCheckExists("0"));
        Assert.assertEquals("", getAndRemoveStringFromDebugLogCheckExists("0_2"));
        Assert.assertEquals("abc", getAndRemoveStringFromDebugLogCheckExists("1"));
        Assert.assertEquals("def", getAndRemoveStringFromDebugLogCheckExists("2"));
        Assert.assertEquals("ghi", getAndRemoveStringFromDebugLogCheckExists("3"));
        Assert.assertEquals("Hello \"world\"", getAndRemoveStringFromDebugLogCheckExists("4"));
        Assert.assertEquals("1@23", getAndRemoveStringFromDebugLogCheckExists("5"));
        Assert.assertEquals("first line\nsecond line\nthird line", getAndRemoveStringFromDebugLogCheckExists("multiline"));

        checkNothingLeftInDebugLog();
    }

    @Test
    public void testPrimitiveTypes() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/02_primitiveTypesTest.script");
        Assert.assertEquals(new MutableVar(985), getAndRemoveDebugLogCheckExists("long"));
        Assert.assertEquals(new MutableVar(-985), getAndRemoveDebugLogCheckExists("long-"));
        Assert.assertEquals(new MutableVar(98.98), getAndRemoveDebugLogCheckExists("double"));
        Assert.assertEquals(new MutableVar(-98.98), getAndRemoveDebugLogCheckExists("double-"));
        Assert.assertEquals(new MutableVar(true), getAndRemoveDebugLogCheckExists("booleanTrue"));
        Assert.assertEquals(new MutableVar(false), getAndRemoveDebugLogCheckExists("booleanFalse"));
        Assert.assertEquals("StringValue", getAndRemoveStringFromDebugLogCheckExists("string"));

        checkNothingLeftInDebugLog();
    }

    @Test
    public void testAddNumbers() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/03_addNumbersTest.script");
        Assert.assertEquals(698, getAndRemoveLongFromDebugLogCheckExists("(698)"));
        Assert.assertEquals(1 + 6, getAndRemoveLongFromDebugLogCheckExists("decimal_1"));
        Assert.assertEquals(9 * 6, getAndRemoveLongFromDebugLogCheckExists("decimal_2"));
        Assert.assertEquals(12 / 3, getAndRemoveLongFromDebugLogCheckExists("decimal_3"));
        Assert.assertEquals(12 - 9, getAndRemoveLongFromDebugLogCheckExists("decimal_4"));
        Assert.assertEquals(12 % 5, getAndRemoveLongFromDebugLogCheckExists("decimal_5"));

        Assert.assertEquals(1.5 + 6.5, getAndRemoveDoubleFromDebugLogCheckExists("double_1"), 0.001);
        Assert.assertEquals(9.5 * 6.5, getAndRemoveDoubleFromDebugLogCheckExists("double_2"), 0.001);
        Assert.assertEquals(12.5 / 2.2, getAndRemoveDoubleFromDebugLogCheckExists("double_3"), 0.001);
        Assert.assertEquals(12.9 - 0.9, getAndRemoveDoubleFromDebugLogCheckExists("double_4"), 0.001);

        checkNothingLeftInDebugLog();
    }

    @Test
    public void testFunctionCall() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/04_functionCallTest.script");
        Assert.assertEquals(new MutableVar(45), getAndRemoveDebugLogCheckExists("returnFromFunctionWithArg"));
        Assert.assertEquals(new MutableVar(77), getAndRemoveDebugLogCheckExists("return2ValuesFromFunctionWithArg"));
        Assert.assertEquals(new MutableVar(77), getAndRemoveDebugLogCheckExists("return2ValuesFromFunctionWithArgX1"));
        Assert.assertEquals(new MutableVar(26), getAndRemoveDebugLogCheckExists("return2ValuesFromFunctionWithArgX2"));

        Assert.assertEquals(new MutableVar(78), getAndRemoveDebugLogCheckExists("returnFromFunctionValue"));
        Assert.assertEquals(new MutableVar(78), getAndRemoveDebugLogCheckExists("returnFromFunctionValueX"));
        Assert.assertEquals(new MutableVar(25), getAndRemoveDebugLogCheckExists("passOneArgument"));
        Assert.assertEquals(new MutableVar(65), getAndRemoveDebugLogCheckExists("passNoArguments"));
        Assert.assertEquals(new MutableVar(40), getAndRemoveDebugLogCheckExists("passTwoArgumentsX"));
        Assert.assertEquals(new MutableVar(60), getAndRemoveDebugLogCheckExists("passTwoArgumentsY"));

        checkNothingLeftInDebugLog();
    }

    @Test
    public void testBitOperations() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/05_bitExpressionTest.script");
        Assert.assertEquals(98 & 28, getAndRemoveLongFromDebugLogCheckExists("bitAnd"));
        Assert.assertEquals(98 | 28, getAndRemoveLongFromDebugLogCheckExists("bitOr"));
        Assert.assertEquals(98 ^ 28, getAndRemoveLongFromDebugLogCheckExists("bitXor"));
    }

    @Test
    public void testComparisonOperations() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/06_comparisonTest.script");
        //all keys that contains + should have true value
        for (String key : DebugLog.getKeys()) {
            boolean value = ((MutableVar) DebugLog.getValue(key)).getAsBoolean();
            if (key.contains("+")) {
                Assert.assertTrue("Key [" + key + "] should be true but it is false", value);
            } else if (key.contains("-")) {
                Assert.assertFalse("Key [" + key + "] should be false but it is true", value);
            } else {
                throw new IllegalArgumentException("Key [" + key + "] does not have + or -, but should for correct assertion");
            }
        }
    }

    @Test
    public void testNotOperations() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/07_notTest.script");
        Assert.assertEquals(false, getAndRemoveBooleanFromDebugLogCheckExists("!(true)"));
        Assert.assertEquals(true, getAndRemoveBooleanFromDebugLogCheckExists("!(false)"));
        Assert.assertEquals(false, getAndRemoveBooleanFromDebugLogCheckExists("!(funtionThatReturnsTrue())"));
        Assert.assertEquals(true, getAndRemoveBooleanFromDebugLogCheckExists("!(funtionThatReturnsFalseAndTrue())"));
    }

    @Test
    public void testIf() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/08_if.script");
        Assert.assertEquals(2, getAndRemoveLongFromDebugLogCheckExists("afterif"));
        Assert.assertEquals(1, getAndRemoveLongFromDebugLogCheckExists("ifbody"));
        Assert.assertFalse(DebugLog.contains("ifbody2"));

        Assert.assertEquals(3, getAndRemoveLongFromDebugLogCheckExists("ifWithElse_Body"));
        Assert.assertFalse(DebugLog.contains("ifWithElse_Else"));
        Assert.assertEquals(6, getAndRemoveLongFromDebugLogCheckExists("ifWithElse_Else2"));
        Assert.assertFalse(DebugLog.contains("ifWithElse_Body2"));

        Assert.assertEquals(12, getAndRemoveLongFromDebugLogCheckExists("ifElseIf_3"));
        Assert.assertEquals(14, getAndRemoveLongFromDebugLogCheckExists("ifElseIf_5"));
        Assert.assertEquals(16, getAndRemoveLongFromDebugLogCheckExists("ifElseIf_7"));
        Assert.assertFalse(DebugLog.contains("ifWithElse_Body2"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_1"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_2"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_4"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_6"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_8"));
        Assert.assertFalse(DebugLog.contains("ifElseIf_9"));
    }

    @Test
    public void testAssignVariable() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/09_assign_to_var.script");
        Assert.assertEquals(10, getAndRemoveLongFromDebugLogCheckExists("assign_result"));
    }

    @Test
    public void testFor() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/10_for.script");
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(i, getAndRemoveLongFromDebugLogCheckExists("sum_result" + i));
        }

        Assert.assertEquals(" 0123456789", getAndRemoveStringFromDebugLogCheckExists("collected_string"));
        checkNothingLeftInDebugLog();
    }

    @Test
    public void testTable() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/11_table.script");
        Assert.assertEquals("some_value", getAndRemoveStringFromDebugLogCheckExists("valueFromTable"));
        Assert.assertEquals("nestedKeyValue", getAndRemoveStringFromDebugLogCheckExists("nestedValue"));
        checkNothingLeftInDebugLog();
    }

    @Test
    public void testExternalFunctions() throws IOException {
        Interpreter interpreter = new Interpreter();
        final MutableVar valueToTest = new MutableVar(false);
        final MutableVar testReturnVoidFunctionResult = new MutableVar(false);
        interpreter.getPlatformFunctions().registerFunctionsObject(new Object() {
            @ExternalFunction(name = "testIntSum")
            public long testIntSumFunction(long a, long b) {
                return a + b;
            }

            @ExternalFunction(name = "testStringFunction")
            public String testReturnString(String a) {
                String result = "_" + a + "_";
                valueToTest.setStringValue(result);
                return result;
            }

            @ExternalFunction(name = "testReturnVoidFunction")
            public void testReturnVoidFunction(String a) {
                testReturnVoidFunctionResult.setStringValue(a);
            }
        });

        interpreter.compileAndExecute("resource:///com/scriptlang/testdata/12_external_functions.script");
        Assert.assertEquals("_abc_", (String) valueToTest.value);
        Assert.assertEquals("_abc_", getAndRemoveStringFromDebugLogCheckExists("resultOfStringFunction"));
        Assert.assertEquals(25, getAndRemoveLongFromDebugLogCheckExists("resultOfSumFunction"));
        Assert.assertEquals("toVoidValue", (String) testReturnVoidFunctionResult.value);

        checkNothingLeftInDebugLog();
    }
    
    @Test
    public void testGlobalVariables() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/13_global_variables.script");
        Assert.assertEquals(1, getAndRemoveLongFromDebugLogCheckExists("originalGlobalX"));
        Assert.assertEquals(100, getAndRemoveLongFromDebugLogCheckExists("originalGlobalY"));
        Assert.assertEquals(12, getAndRemoveLongFromDebugLogCheckExists("modifiedGlobalX"));
        Assert.assertEquals(23, getAndRemoveLongFromDebugLogCheckExists("modifiedGlobalY"));
        
        checkNothingLeftInDebugLog();
    }
    
    @Test
    @Ignore
    public void testFibonacchi() throws IOException {
        new Interpreter().compileAndExecute("resource:///com/scriptlang/testdata/fibCalc.script");
        //checkNothingLeftInDebugLog();
    }
}
