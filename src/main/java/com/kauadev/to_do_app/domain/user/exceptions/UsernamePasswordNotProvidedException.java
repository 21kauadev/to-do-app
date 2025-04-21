package com.kauadev.to_do_app.domain.user.exceptions;

public class UsernamePasswordNotProvidedException extends RuntimeException {

    public UsernamePasswordNotProvidedException() {
        super("Nome de usuário (username) ou senha não fornecidos.");
    }

    public UsernamePasswordNotProvidedException(String msg) {
        super(msg);
    }

}
