package org.coder.concurrency.programming.pattern._6_guarded_suspension.alarm;

public enum AlarmType {
    FAULT("fault"), RESUME("resume");

    private final String name;

    private AlarmType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}