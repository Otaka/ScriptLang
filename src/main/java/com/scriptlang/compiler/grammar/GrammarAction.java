package com.scriptlang.compiler.grammar;

import com.scriptlang.compiler.ast.BaseAst;
import com.scriptlang.compiler.ast.BooleanValueAst;
import com.scriptlang.compiler.ast.StringAst;
import com.scriptlang.compiler.ast.LongValueAst;
import com.scriptlang.compiler.ast.DoubleValueAst;
import com.scriptlang.compiler.ast.TokenAst;
import com.scriptlang.exceptions.ParseException;
import com.scriptlang.utils.Utils;
import java.lang.reflect.Method;
import java.util.*;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.support.Position;
import org.parboiled.support.ValueStack;

/**
 * @author Dmitry
 */
public class GrammarAction extends BaseParser<Object> {

    private Map<String, Method> strToMethodCacheMap = new HashMap<>();
    private final String errorMessageTemplate = "[Line:%1$d Column:%2$d] %3$s. Found:\"%4$s\"";

    protected void push(Context context, Object obj) {
        ValueStack valueStack = context.getValueStack();
        valueStack.push(obj);
    }

    protected <T> T peekPopObjectFromStack(Context context, Class<T> clazz, boolean pop) {
        return peekPopObjectFromStack(context, clazz, pop, 0);
    }

    protected <T> T peekPopObjectFromStack(Context context, Class<T> clazz, boolean pop, int index) {
        ValueStack valueStack = context.getValueStack();
        if (valueStack.isEmpty()) {
            throwInternalError("You want to get " + clazz.getSimpleName() + " from stack, but it is empty");
        }

        Object object;
        if (pop) {
            object = valueStack.pop(index);
        } else {
            object = valueStack.peek(index);
        }
        if (!clazz.isAssignableFrom(object.getClass())) {
            throwInternalError("You want to get " + clazz.getSimpleName() + " from stack, but top[" + index + "] element has type " + object.getClass().getSimpleName());
        }

        return (T) object;
    }

    protected void checkStackTopContainsObject(Context context, Class clazz) {
        peekObject(context, clazz);
    }

    protected <T> T peekObject(Context context, Class<T> clazz) {
        return peekPopObjectFromStack(context, clazz, false);
    }

    protected <T> T popObject(Context context, Class<T> clazz) {
        return peekPopObjectFromStack(context, clazz, true);
    }

    protected <T> T popObject(Context context, Class<T> clazz, int index) {
        return peekPopObjectFromStack(context, clazz, true, index);
    }

    protected void throwInternalError(String message) {
        throw new IllegalStateException("Internal Error: " + message);
    }

    public Action actionFail(final String message) {
        return (Action) (Context context1) -> {
            String fullMessage = message;
            if (fullMessage.contains("$match")) {
                fullMessage = fullMessage.replace("$match", context1.getMatch());
            }

            int currentIndex = context1.getCurrentIndex();
            Position position = context1.getInputBuffer().getPosition(currentIndex);
            String part = context1.getInputBuffer().extract(currentIndex, currentIndex + 20);
            part = part.replace("\n", "\\n");
            Formatter formatter = new Formatter();
            formatter.format(errorMessageTemplate, position.line, position.column, fullMessage, part);
            fullMessage = formatter.toString();
            throw new ParseException(currentIndex, fullMessage);
        };
    }

    public Action dbgPrint(final String message) {
        return (Action) (Context context) -> {
            String m = message;
            if (m.contains("$match")) {
                m = m.replace("$match", context.getMatch());
            }
            if (m.contains("$current")) {
                m = m.replace("$current", "" + context.getCurrentIndex() + ":" + context.getCurrentChar());
            }

            System.out.println(m);
            return true;
        };
    }

    Action actionFailWithFlag(boolean flag, String errorMessage) {
        if (flag) {
            return actionFail(errorMessage);
        } else {
            return new Action() {
                @Override
                public boolean run(Context context) {
                    return false;
                }
            };
        }
    }

    public boolean tokenAction(Context context) {
        TokenAst token = new TokenAst(context.getMatch());
        token.setStartPosition(context.getMatchStartIndex());
        token.setEndPosition(context.getMatchEndIndex());
        push(context, token);
        return true;
    }

    private Method searchDeclaredMethod(Class clazz, String methodName, Class... args) throws NoSuchMethodException {
        for (Method m : clazz.getDeclaredMethods()) {
            if (!m.getName().equals(methodName)) {
                continue;
            }
            if (m.getParameterTypes().length != args.length) {
                continue;
            }
            boolean found = true;
            for (int i = 0; i < args.length; i++) {
                if (!m.getParameterTypes()[i].isAssignableFrom(args[i])) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return m;
            }
        }

        String params = Utils.joinStrings(args, ",");
        throw new NoSuchMethodException("Cannot find method " + methodName + "(" + params + ") in class " + clazz.getSimpleName());
    }

    private Method searchMethodInClassAndSuperClasses(Class clazz, String methodName, Class... args) {
        while (clazz != null) {
            try {
                //return clazz.getDeclaredMethod(methodName, args);
                return searchDeclaredMethod(clazz, methodName, args);
            } catch (NoSuchMethodException ex) {

            } catch (SecurityException ex) {
                ex.printStackTrace();
            }

            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public boolean pushNewObject(Class clazz) {
        try {
            BaseAst newObject = (BaseAst) clazz.newInstance();
            push(getContext(), newObject);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error while instantiate class " + clazz.getSimpleName(), ex);
        }
    }

    private Method getSetterMethod(Class clazz, String receiverMethod, Class... args) {
        String key = clazz.getSimpleName() + "|" + receiverMethod;
        Method method = strToMethodCacheMap.get(key);
        if (method != null) {
            return method;
        }

        method = searchMethodInClassAndSuperClasses(clazz, receiverMethod, args);
        if (method == null) {
            String argsString = Arrays.deepToString(args).replace("[", "(").replace("]", ")");
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " do not have method " + receiverMethod + argsString);
        }

        strToMethodCacheMap.put(key, method);
        method.setAccessible(true);
        return method;
    }

    public boolean popTopToNextTop(Class receiverClass, String receiverMethod, Class topObjectClass, String label) {
        try {
            Object topObject = popObject(getContext(), topObjectClass);
            Object receiver = peekObject(getContext(), receiverClass);
            Method method = getSetterMethod(receiverClass, receiverMethod, topObjectClass);
            method.invoke(receiver, topObject);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error(label='" + label + "')", ex);
        }
    }

    public boolean pushString(Context context) {
        String value = context.getMatch().trim();
        value = stripQuotes(value);
        value = processEscapement(value);
        StringAst stringLiteral = new StringAst(value);
        context.getValueStack().push(stringLiteral);
        return true;
    }

    private String stripQuotes(String value) {
        if(value.equals("\"\"")){
            return "";
        }
        if (value.length() > 2 && value.startsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        if (value.length() >= 3 && value.startsWith("`")) {
            char secondChar = value.charAt(1);
            char lastChar = value.charAt(value.length() - 1);
            if (secondChar != lastChar) {
                throw new IllegalStateException("Something wrong with string {" + value + "} [C...C. Second char not equals to last char");
            }
            return value.substring(2, value.length() - 1);
        }

        throw new IllegalStateException("For some reason string {" + value + "} does not have start and end quotes");
    }

    public static String processEscapement(String value) {
        if (value.contains("`")) {
            StringBuilder sb = new StringBuilder();
            boolean escape = false;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (escape) {
                    switch (c) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case '`':
                            sb.append('`');
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                    escape = false;
                } else if (c == '`') {
                    escape = true;
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        } else {
            return value;
        }
    }

    public boolean pushHexInt(Context context) {
        long value = Long.parseUnsignedLong(context.getMatch().trim(), 16);
        context.getValueStack().push(new LongValueAst(value));
        return true;
    }

    public boolean pushBinInt(Context context) {
        long value = Long.parseUnsignedLong(context.getMatch().trim(), 2);
        context.getValueStack().push(new LongValueAst(value));
        return true;
    }

    public boolean pushInteger(Context context) {
        String matched = context.getMatch().trim();

        long value;
        if (matched.startsWith("-")) {
            value = Long.parseLong(matched);
        } else {
            value = Long.parseUnsignedLong(matched);
        }

        context.getValueStack().push(new LongValueAst(value));
        return true;
    }

    public boolean pushFloat(Context context) {
        double value = Double.parseDouble(context.getMatch().trim());
        context.getValueStack().push(new DoubleValueAst(value));
        return true;
    }

    public boolean pushBoolean() {
        String strValue = getContext().getMatch().trim();
        boolean value;
        if (strValue.equals("true")) {
            value = true;
        } else if (strValue.equals("false")) {
            value = false;
        } else {
            throw new IllegalArgumentException("Cannot parse boolean as boolean(true|false) [" + strValue + "]");
        }
        getContext().getValueStack().push(new BooleanValueAst(value));
        return true;
    }

    public boolean popSecondObjectToTop(Class receiverClass, String receiverMethod, Class secondObjectClass) {
        try {
            Object secondObject = popObject(getContext(), secondObjectClass, 1);
            Object receiver = peekObject(getContext(), receiverClass);
            Method method = getSetterMethod(receiverClass, receiverMethod, secondObjectClass);
            method.invoke(receiver, secondObject);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error", ex);
        }
    }

    public boolean setTopTokenToNextTop(Class receiverClass, String receiverMethod) {
        try {
            TokenAst token = popObject(getContext(), TokenAst.class);
            Object receiver = peekObject(getContext(), receiverClass);
            Method method = getSetterMethod(receiverClass, receiverMethod, String.class);
            method.invoke(receiver, token.getValue());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error", ex);
        }
    }

    public boolean setMatch(Context context, Class receiverClass, String receiverMethod) {
        try {
            Object receiver = peekObject(context, receiverClass);
            Method method = getSetterMethod(receiverClass, receiverMethod, String.class);
            method.invoke(receiver, context.getMatch().trim());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error", ex);
        }
    }

    public boolean setFlagToTopObject(Context context, Class receiverClass, String receiverMethod, boolean flag) {
        try {
            Object receiver = peekObject(context, receiverClass);
            Method method = getSetterMethod(receiverClass, receiverMethod, boolean.class);
            method.invoke(receiver, flag);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error", ex);
        }
    }

    public boolean callMethodOnTopObject(Context context, Class topObjectClass, String methodName) {
        try {
            Object receiver = peekObject(context, topObjectClass);
            Method method = getSetterMethod(topObjectClass, methodName, new Class[0]);
            method.invoke(receiver, new Object[0]);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Push match error", ex);
        }
    }
}
