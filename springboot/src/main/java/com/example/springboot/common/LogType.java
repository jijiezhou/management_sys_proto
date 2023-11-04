package com.example.springboot.common;

/**
 * Enum operation for Logs
 */
public enum LogType {

    ADD("add"),UPDATE("update"),DELETE("delete"), BATCH_DELETE("batch_delete"),LOGIN("login"),REGISTER("register");

    private String value;

    public String getValue() {
        return value;
    }

    LogType(String value) {
        this.value = value;
    }
}
