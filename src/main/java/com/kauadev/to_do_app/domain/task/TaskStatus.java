package com.kauadev.to_do_app.domain.task;

public enum TaskStatus {

    PENDING("pending"),
    COMPLETED("completed");

    public String status;

    TaskStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
