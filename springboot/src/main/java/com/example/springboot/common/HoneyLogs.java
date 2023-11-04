package com.example.springboot.common;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HoneyLogs {
    // operation name
    String operation();

    // operation type(ENUM)
    LogType type();
}
