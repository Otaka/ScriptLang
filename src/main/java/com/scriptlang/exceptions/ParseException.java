package com.scriptlang.exceptions;

/**
 * @author Dmitry
 */
public class ParseException extends RuntimeException {

    private int position;

    public ParseException(int position, String message) {
        super(message);
        this.position = position;
    }

}
