package com.scriptlang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry
 */
public class DebugLog {

    private static Map<String, Object> keyValueMap = new HashMap<>();

    public static Map<String, Object> getKeyValueMap() {
        return keyValueMap;
    }

    public static void clear() {
        keyValueMap.clear();
    }

    public static List<String> getKeys() {
        return new ArrayList<>(keyValueMap.keySet());
    }

    public static Object getValue(String key) {
        return keyValueMap.get(key);
    }

    public static boolean contains(String key) {
        return keyValueMap.containsKey(key);
    }
}
