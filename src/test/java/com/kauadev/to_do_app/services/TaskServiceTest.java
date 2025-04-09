package com.kauadev.to_do_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.domain.task.TaskStatus;
import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.domain.user.exceptions.UserCanNotSeeOtherUsersTasks;
import com.kauadev.to_do_app.repositories.TaskRepository;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should get all tasks sucessfully when everything is OK")
    void getAllTasksCase1() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.ADMIN, null);

        Task task1 = new Task(UUID.randomUUID(), "Tarefa de teste", "description", LocalDate.now(), TaskStatus.PENDING,
                loggedUser);
        Task task2 = new Task(UUID.randomUUID(), "Tarefa de teste2", "description", LocalDate.now(), TaskStatus.PENDING,
                loggedUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext); // substitui o real pelo mockado
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        // testar a checagem das authorities não será necessária no caso de sucesso.

        when(this.taskRepository.findAll()).thenReturn(List.of(task1, task2));

        List<Task> result = this.taskService.getAllTasks();

        assertEquals(result.get(0).getTitle(), task1.getTitle());
        assertEquals(result.get(1).getTitle(), task2.getTitle());
    }

    @Test
    @DisplayName("Should throw UserCanNotSeeOtherUsersTasksException when user is not an ADMIN")
    void getAllTasksCase2() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(loggedUser);

        Exception thrown = Assertions.assertThrows(UserCanNotSeeOtherUsersTasks.class, () -> {
            this.taskService.getAllTasks();
        });

        assertEquals("Usuário comum não pode ver as tarefas de todos os usuários.", thrown.getMessage());
    }
}
