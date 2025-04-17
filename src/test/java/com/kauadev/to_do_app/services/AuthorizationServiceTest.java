package com.kauadev.to_do_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.repositories.UserRepository;

public class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should load a user by username when everything is OK")
    void loadUserByUsernameCase1() {
        String username = "username_test";
        User user = new User(username, "123456789", UserRole.USER);

        when(this.userRepository.findByUsername(username)).thenReturn(user);

        UserDetails result = this.authorizationService.loadUserByUsername(username);

        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void loadUserByUsernameCase2() {
        // supondo que não exista.
        String username = null;

        UsernameNotFoundException thrown = Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            this.authorizationService.loadUserByUsername(username);
        });

        assertEquals("Usuário não encontrado.", thrown.getMessage());
    }
}
