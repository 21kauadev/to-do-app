package com.kauadev.to_do_app.domain.user.exceptions;

public class ADMCanNotCreateTaskException extends RuntimeException {

    public ADMCanNotCreateTaskException(String msg) {
        super(msg);
    }

    public ADMCanNotCreateTaskException() {
        super("ADMs não podem criar tarefas.");
    }
}
