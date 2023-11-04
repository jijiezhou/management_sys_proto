package com.example.springboot.common;

/**
 * Enum operation for Logs
 */
public enum LogType {

    ADD("add"),UPDATE("edit"),DELETE("delete"), BATCH_DELETE("delbatch"),LOGIN("login"),REGISTER("register");

    private String value;

    public String getValue() {
        return value;
    }

    LogType(String value) {
        this.value = value;
    }
}
