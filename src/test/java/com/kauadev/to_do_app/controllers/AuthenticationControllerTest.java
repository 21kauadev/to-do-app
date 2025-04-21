package com.kauadev.to_do_app.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kauadev.to_do_app.domain.user.LoginDTO;
import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.domain.user.exceptions.UsernamePasswordNotProvidedException;
import com.kauadev.to_do_app.infra.security.TokenService;
import com.kauadev.to_do_app.repositories.UserRepository;

public class AuthenticationControllerTest {

    // mocks -> objetos falsos. copiam a estrutura da classe, mas não sua lógica.

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;

    // injetando os mocks no objeto real de authenticationController
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

    @Test
    @DisplayName("Should log in and return a token when everything is OK")
    void loginCase1() {
        User userTryingToLogin = new User(1, "kaua", "123456789", UserRole.USER, null);
        Authentication authentication = mock(Authentication.class); // mockando classe Authentication

        LoginDTO loginData = new LoginDTO("kaua", "123456789");
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(
                loginData.username(), loginData.password());

        // mocks são só uma cópia do objeto, da estrutura, porém não da lógica deles,
        // sendo assim,
        // deixamos claro pros mocks (objetos falsos) o que deve ser retornado na
        // chamada dos métodos
        when(this.authenticationManager.authenticate(usernamePassword)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userTryingToLogin);

        // o programa sabe o que retornar no get principal pois foi definido acima, no
        // when.
        User user = (User) authentication.getPrincipal();

        // nesse caso, o valor não importa. só que seja um dado do tipo esperado
        String supposedTokenToReturn = "token_returned_fdaojfapodsid";

        // deve retornar um token, logo deixamos explícito
        when(this.tokenService.generateToken(user)).thenReturn(supposedTokenToReturn);

        // aqui, chamamos o método REAL, com lógica, que está sendo testada
        ResponseEntity<String> response = this.authenticationController.login(loginData);

        // abaixo são as verificações após a chamada do método.
        // cofnirmar se foi retornado o que devia ser retornado, se tal métood de fato
        // foi chamado, etc

        // se o token gerado foi igual ao retornado
        // se o status retornado foi igual ao esperado.
        assertEquals(supposedTokenToReturn, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // se o teste roda, tudo correu como esperado.
    }

    @Test
    @DisplayName("Should throw UsernamePasswordNotProvidedException when username or password is not provided")
    void loginCase2() {
        // segundo dado null pra exemplificar um caso onde algum dos dados não esteja
        // presente.
        LoginDTO loginData = new LoginDTO("kaua", null);

        // deve lançar a exceção esperada caso não haja ou username ou password
        UsernamePasswordNotProvidedException usernamePasswordNotProvidedException = Assertions
                .assertThrows(UsernamePasswordNotProvidedException.class, () -> {
                    this.authenticationController.login(loginData);
                });

        // verifica o retorno, garantidno que seja igual ao esperado.
        assertEquals("Nome de usuário (username) ou senha não fornecidos.",
                usernamePasswordNotProvidedException.getMessage());
    }

}
