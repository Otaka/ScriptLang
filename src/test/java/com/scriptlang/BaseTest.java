package com.scriptlang;

import com.scriptlang.data.MutableVar;
import com.scriptlang.data.VariableType;
import org.junit.After;

/**
 * @author Dmitry
 */
public class BaseTest {

    @After
    public void cleanDebugLog() {
        DebugLog.clear();
    }

    protected long getAndRemoveLongFromDebugLogCheckExists(String key) {
        return getAndRemoveDebugLogCheckExists(key).getAsLong();
    }

    protected boolean getAndRemoveBooleanFromDebugLogCheckExists(String key) {
        return getAndRemoveDebugLogCheckExists(key).getAsBoolean();
    }

    protected double getAndRemoveDoubleFromDebugLogCheckExists(String key) {
        return getAndRemoveDebugLogCheckExists(key).getAsDouble();
    }
    
    protected String getAndRemoveStringFromDebugLogCheckExists(String key) {
        return getAndRemoveDebugLogCheckExists(key).getAsString();
    }

    
    
    protected MutableVar getAndRemoveDebugLogCheckExists(String key) {
        if (!DebugLog.contains(key)) {
            throw new IllegalArgumentException("Key [" + key + "] not found");
        }

        return (MutableVar) DebugLog.getKeyValueMap().remove(key);
    }

    protected void checkNothingLeftInDebugLog() {
        if (!DebugLog.getKeys().isEmpty()) {
            throw new IllegalStateException("Some keys in DebugLog still left " + DebugLog.getKeys());
        }
    }
}
