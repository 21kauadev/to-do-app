package com.kauadev.to_do_app.domain.task.exceptions;

public class OtherUserTasksCantBeUpdatedException extends RuntimeException {
    public OtherUserTasksCantBeUpdatedException(String msg) {
        super(msg);
    }

    public OtherUserTasksCantBeUpdatedException() {
        super("Tarefas de outros usuários não podem ser atualizadas.");
    }

}
