package com.kauadev.to_do_app.domain.task.exceptions;

public class OtherUserTasksCantBeDeletedException extends RuntimeException {

    public OtherUserTasksCantBeDeletedException(String msg) {
        super(msg);
    }

    public OtherUserTasksCantBeDeletedException() {
        super("Tarefas de outros usuarios nao podem ser deletadas.");
    }
}
