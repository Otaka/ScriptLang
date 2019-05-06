package com.scriptlang.utils;

/**
 * @author Dmitry
 */
public class Utils {

    public static String joinStrings(Object[] values, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object obj : values) {
            if (first == false) {
                sb.append(separator);
            }
            first = false;
            sb.append(obj.toString());
        }
        return sb.toString();
    }
}
