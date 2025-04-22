package com.kauadev.to_do_app.infra.security;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.kauadev.to_do_app.repositories.UserRepository;

public class JwtAuthFilterTest {

    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HandlerExceptionResolver resolver;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
