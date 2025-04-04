package com.kauadev.to_do_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.domain.user.exceptions.UserNotFoundException;
import com.kauadev.to_do_app.repositories.UserRepository;

// classe para testar o UserService.

// testes e unitários e tratamento de exceções podem ser similares, mas tem propósitos diferentes

// testes unitários -> buscam testar a aplicação antes mesmo de ela estar em produção, evitando que erros aconteçam.
// tratamento de exceções -> é a resposta amigável pro usuário pra exibir pro usuário justamente no momento que o erro ocorre.

public class UserServiceTest {

    // do mockito.
    // classes Mockadas -> mesma estrutura, mas sem lógica nos métodos. são só uma
    // cópia, um objeto simulado que imita o comportamneto do objeto original
    // objeto falso
    @Mock
    private UserRepository userRepository;

    // injeta na classe REAL userService os MOCKS, no caso, o user repository
    // mockado. para testarmos os métodos de user service.

    // como queremos testar a lógica do userService, ele precisa ser uma classe
    // REAL, com implementação e lógica nos métodos.

    // objeto real, com o mock injetado
    @InjectMocks
    private UserService userService;

    // Se é um mock, não está sendo testado. são usados para testar

    // antes de iniciar os testes unitarios
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should get a user successfully when everything is OK")
    void getUserCase1() throws Exception {
        User foundUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        // mockamos o userRepository pois ele faz parte da lógica de get User dentro do
        // userService.
        // logo, deve estar aqui também, retornando o que supostamente retornaria no
        // método original.
        when(this.userRepository.findById(1)).thenReturn(Optional.of(foundUser));

        User result = this.userService.getUser(1);

        assertEquals("kaua", result.getUsername());
    }

    @Test
    @DisplayName("Should throw Exception when user is not found")
    void getUserCase2() throws Exception {
        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        // certificar-se de que ele lançou a exceção esperda
        // vai tentar pegar uma exceção do tipo esperado. se não, teste falha
        Exception thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.userService.getUser(1);
        });

        Assertions.assertEquals("Usuário não encontrado.", thrown.getMessage());
    }
}
