package com.scriptlang.ffi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Dmitry
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalFunction {

    String name();
}
