package com.scriptlang.data;

import com.scriptlang.exceptions.ExecutionException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Dmitry
 */
public class MutableVar {

    public VariableType variableType;
    public Object value;

    public MutableVar(HashMap value){
        variableType=VariableType.TABLE;
        this.value=value;
    }
    
    public MutableVar(long value) {
        variableType = VariableType.INT;
        this.value = value;
    }

    public MutableVar(String value) {
        variableType = VariableType.STRING;
        this.value = value;
    }

    public MutableVar(double value) {
        variableType = VariableType.DOUBLE;
        this.value = value;
    }

    public MutableVar(boolean value) {
        variableType = VariableType.BOOLEAN;
        this.value = value;
    }

    public MutableVar(VariableType variableType, Object value) {
        this.variableType = variableType;
        this.value = value;
    }

    public void setBooleanValue(boolean value) {
        this.value = value;
        variableType = VariableType.BOOLEAN;
    }

    public void setLongValue(long value) {
        this.value = value;
        variableType = VariableType.INT;
    }

    public void setStringValue(String value) {
        this.value = value;
        variableType = VariableType.STRING;
    }

    public void setDoubleValue(double value) {
        this.value = value;
        variableType = VariableType.DOUBLE;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public Object getValue() {
        return value;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public long getAsLong() {
        if (variableType == VariableType.INT) {
            return (long) value;
        }
        if (variableType == VariableType.DOUBLE) {
            return (long) (double) value;
        }
        throw new ExecutionException("Cannot represent " + variableType + ":[" + value + "] as long");
    }

    public double getAsDouble() {
        if (variableType == VariableType.INT) {
            return (double) (long) value;
        }
        if (variableType == VariableType.DOUBLE) {
            return (double) value;
        }
        throw new ExecutionException("Cannot represent " + variableType + ":[" + value + "] as double");
    }

    public String getAsString() {
        return value.toString();
    }

    public boolean getAsBoolean() {
        if (variableType == VariableType.BOOLEAN) {
            return (boolean) value;
        }
        throw new ExecutionException("Cannot represent " + variableType + ":[" + value + "] as boolean");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MutableVar)) {
            return false;
        }
        MutableVar rhs = (MutableVar) obj;
        if (variableType != rhs.variableType) {
            return false;
        }

        return value.equals(rhs.value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.variableType);
        hash = 71 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString() {
        return variableType + ":" + value;
    }

}
