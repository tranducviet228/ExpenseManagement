package com.kma.project.expensemanagement.enums;

public enum NotificationParameter {

    SOUND("default"),
    COLOR("#FFFF00");

    private final String value;

    NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
