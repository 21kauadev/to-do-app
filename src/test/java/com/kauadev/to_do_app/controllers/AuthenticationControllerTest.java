package com.kauadev.to_do_app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.infra.security.TokenService;
import com.kauadev.to_do_app.repositories.UserRepository;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService TokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register and create a user when everything is OK")
    void registerCase1() {
        String password = "password";
        String encodedPassword = "encoded_password";

        // retorna uma string, no fim das contas
        when(this.passwordEncoder.encode(password)).thenReturn(encodedPassword);

        UserDTO userDTO = new UserDTO("kaua", encodedPassword, UserRole.USER);

        ResponseEntity<String> result = this.authenticationController.register(userDTO);

        verify(this.userRepository, times(1)).save(any(User.class));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Usuário registrado!", result.getBody());
    }

    @Test
    @DisplayName("Should return bad request and a message in the body when user is already registered")
    void registerCase2() {
        UserDTO userDTO = new UserDTO("kaua", "encoded_password", UserRole.USER);
        User userAlreadyRegistered = new User(userDTO.username(), userDTO.password(), userDTO.role());

        when(this.userRepository.findByUsername(userDTO.username())).thenReturn(userAlreadyRegistered);

        ResponseEntity<String> result = this.authenticationController.register(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Usuário já está registrado.", result.getBody());
    }

}
