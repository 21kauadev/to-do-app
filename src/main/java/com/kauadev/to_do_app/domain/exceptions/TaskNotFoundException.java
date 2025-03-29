package com.kauadev.to_do_app.domain.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String msg) {
        super(msg);
    }

    public TaskNotFoundException() {
        super("Tarefa não encontrada.");
    }
}
