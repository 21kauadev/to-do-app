package com.kauadev.to_do_app.domain.task;

public enum TaskStatus {

    PENDING("pending"),
    COMPLETED("completed");

    public String role;

    TaskStatus(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
