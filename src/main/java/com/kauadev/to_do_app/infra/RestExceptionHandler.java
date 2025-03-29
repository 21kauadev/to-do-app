package com.kauadev.to_do_app.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.kauadev.to_do_app.domain.exceptions.TaskNotFoundException;
import com.kauadev.to_do_app.domain.user.exceptions.ADMCanNotCreateTaskException;
import com.kauadev.to_do_app.domain.user.exceptions.UserCanNotSeeOtherUsersTasks;
import com.kauadev.to_do_app.domain.user.exceptions.UserNotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<RestErrorMessage> userNotFoundHandler(UserNotFoundException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatedError);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    private ResponseEntity<RestErrorMessage> taskNotFoundHandler(TaskNotFoundException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(threatedError);
    }

    @ExceptionHandler(ADMCanNotCreateTaskException.class)
    private ResponseEntity<RestErrorMessage> admCanNotCreateTaskHandler(ADMCanNotCreateTaskException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(threatedError);
    }

    @ExceptionHandler(UserCanNotSeeOtherUsersTasks.class)
    private ResponseEntity<RestErrorMessage> userCanNotSeeOtherUsersTasksHandler(
            UserCanNotSeeOtherUsersTasks exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(threatedError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestErrorMessage> authExceptionHandler(AuthenticationException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.UNAUTHORIZED);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(threatedError);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<RestErrorMessage> verificationExceptionHandler(JWTVerificationException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.UNAUTHORIZED);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(threatedError);
    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<RestErrorMessage> creationExceptionHandler(JWTCreationException exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(threatedError);
    }

    // tratamento de erros mais generalista.
    // pra pegar possiveis erros mais gerais que possam escapar
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorMessage> generalExceptionHandler(Exception exception) {
        RestErrorMessage threatedError = new RestErrorMessage(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(threatedError);
    }
}
