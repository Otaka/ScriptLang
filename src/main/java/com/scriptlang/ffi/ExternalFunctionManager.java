package com.scriptlang.ffi;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitry
 */
public class ExternalFunctionManager {

    private Map<String, ExternalFunctionData> functionNameToFunctionData = new HashMap<>();
    private Set<Class> processedClasses = new HashSet<>();

    public void registerFunctionsObject(Object objectWithFunctions) {
        Class clazz = objectWithFunctions.getClass();
        if (processedClasses.contains(clazz)) {
            throw new IllegalArgumentException("You want to register functions object, but class [" + clazz.getName() + "] already registered");
        }

        for (Method method : clazz.getDeclaredMethods()) {
            ExternalFunction externalFunctionAnnotation = method.getAnnotation(ExternalFunction.class);
            if (externalFunctionAnnotation == null) {
                continue;
            }
            method.setAccessible(true);
            registerMethodAsExternalFunction(objectWithFunctions, clazz, method, externalFunctionAnnotation);
        }
        processedClasses.add(clazz);
    }

    public ExternalFunctionData getExternalFunction(String functionName) {
        return functionNameToFunctionData.get(functionName);
    }

    private void registerMethodAsExternalFunction(Object owner, Class clazz, Method method, ExternalFunction annotation) {
        String functionName = annotation.name();
        ExternalFunctionData externalFunctionData = new ExternalFunctionData(functionName, method, owner, method.getParameterCount());
        if (functionNameToFunctionData.containsKey(functionName)) {
            ExternalFunctionData duplicatedExternalFunctionData = functionNameToFunctionData.get(functionName);
            throw new IllegalStateException("Duplicate external function found [" + functionName + "]. First in [" + owner.getClass().getName() + "]. Second in [" + duplicatedExternalFunctionData.getOwnerObject().getClass().getSimpleName() + "]");
        }
        functionNameToFunctionData.put(functionName, externalFunctionData);
    }

    public static class ExternalFunctionData {

        private String name;
        private Method method;
        private Object ownerObject;
        private int argumentsCount;

        public ExternalFunctionData(String name, Method method, Object ownerObject, int argumentsCount) {
            this.name = name;
            this.method = method;
            this.ownerObject = ownerObject;
            this.argumentsCount = argumentsCount;
        }

        public int getArgumentsCount() {
            return argumentsCount;
        }

        public Method getMethod() {
            return method;
        }

        public String getName() {
            return name;
        }

        public Object getOwnerObject() {
            return ownerObject;
        }
    }
}
