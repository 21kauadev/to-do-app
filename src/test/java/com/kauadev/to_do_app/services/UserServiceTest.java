package com.kauadev.to_do_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserDTO;
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
    @Mock
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("Should get all users succesfully when everything is OK")
    void getAllUsersCase1() throws Exception {
        // supondo que esses são os usuários que tem cadastrados
        User user1 = new User(1, "kaua", "123456789", UserRole.USER, null);
        User user2 = new User(2, "pedrin", "123456789", UserRole.USER, null);

        // mockando o userRepository.
        when(this.userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = this.userService.getAllUsers();

        assertEquals(user1.getUsername(), result.get(0).getUsername());
        assertEquals(user2.getUsername(), result.get(1).getUsername());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return an empty array when the application have no users registered")
    void getAllUsersCase2() throws Exception {
        List<User> emptyUsersList = List.of();

        when(this.userRepository.findAll()).thenReturn(emptyUsersList);

        assertTrue(emptyUsersList.size() == 0);
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

        assertEquals(foundUser.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    void getUserCase2() throws Exception {
        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        // certificar-se de que ele lançou a exceção esperda
        // vai tentar pegar uma exceção do tipo esperado. se não, teste falha
        Exception thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.userService.getUser(1);
        });

        Assertions.assertEquals("Usuário não encontrado.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should update a user successfully when everything is OK")
    void updateUserCase1() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        // mockando ambas classes usadas no processo de obter o objeto principal
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        // acessamos o securityContextHolder real, não mockado
        SecurityContextHolder.setContext(securityContext); // substitui o contexto real por um mockado
        when(authentication.getPrincipal()).thenReturn(loggedUser); // deve retornar o suposto usuário logado (objeto
                                                                    // User)

        when(this.userRepository.findById(1)).thenReturn(Optional.of(loggedUser));

        // supondo que estou atualizando um usuário, enviando os dados do DTO
        UserDTO newUser = new UserDTO("kauaUpdated", "123456789updated", UserRole.USER);

        // já que o encoder geralmente retorna outra string
        // só é preciso verificar se elas batem, ou seja, ser salvo com a nova senha.
        when(this.passwordEncoder.encode(newUser.password())).thenReturn("hashed_password");

        loggedUser.setUsername(newUser.username());
        loggedUser.setPassword("hashed_password");

        this.userService.updateUser(newUser);

        // verificando se foi de fato chamado no user service
        verify(this.userRepository, times(1)).save(loggedUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user to be updated is not found")
    void updateUserCase2() {

        when(this.userRepository.findById(1)).thenReturn(Optional.empty());

        Exception thrown = Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.userService.getUser(1);
        });

        Assertions.assertEquals("Usuário não encontrado.", thrown.getMessage());
    }
}
