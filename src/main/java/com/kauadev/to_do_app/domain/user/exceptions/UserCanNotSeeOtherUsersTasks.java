package com.kauadev.to_do_app.domain.user.exceptions;

public class UserCanNotSeeOtherUsersTasks extends RuntimeException {

    public UserCanNotSeeOtherUsersTasks(String msg) {
        super(msg);
    }

    public UserCanNotSeeOtherUsersTasks() {
        super("Usuário comum não pode ver as tarefas de todos os usuários.");
    }
}
