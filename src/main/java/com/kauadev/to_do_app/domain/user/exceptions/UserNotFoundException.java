package com.kauadev.to_do_app.domain.user.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException() {
        super("Usuário não encontrado.");
    }
}
