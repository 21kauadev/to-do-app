package com.kauadev.to_do_app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
import com.kauadev.to_do_app.domain.task.TaskDTO;
import com.kauadev.to_do_app.domain.task.TaskStatus;
import com.kauadev.to_do_app.domain.task.exceptions.OtherUserTasksCantBeUpdatedException;
import com.kauadev.to_do_app.domain.task.exceptions.TaskNotFoundException;
import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.UserRole;
import com.kauadev.to_do_app.domain.user.exceptions.ADMCanNotCreateTaskException;
import com.kauadev.to_do_app.domain.user.exceptions.UserCanNotSeeOtherUsersTasks;
import com.kauadev.to_do_app.repositories.TaskRepository;

// relembrando:
// case1 == sucess 
// case2 == fail

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    @Test
    @DisplayName("Should get a task succesfully when everything is OK")
    void getTaskCase1() {
        UUID uuid = UUID.randomUUID();

        Task task = new Task(uuid, "Tarefa de teste", "description", LocalDate.now(), TaskStatus.PENDING,
                null);

        when(this.taskRepository.findById(uuid.toString())).thenReturn(Optional.of(task));

        Task result = this.taskService.getTask(task.getId().toString());

        assertEquals(task.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task is not found")
    void getTaskCase2() {

        // optional vazio, sem objeto task encontrado
        when(this.taskRepository.findById(UUID.randomUUID().toString())).thenReturn(Optional.empty());

        TaskNotFoundException thrown = Assertions.assertThrows(TaskNotFoundException.class, () -> {
            this.taskService.getTask(UUID.randomUUID().toString());
        });

        assertEquals("Tarefa não encontrada.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should get all users tasks succesfully when everything is OK")
    void getUserTasksCase1() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);
        User anotherUser = new User(2, "kaua2", "123456789", UserRole.USER, null);

        Task userTask1 = new Task(UUID.randomUUID(), "User id1 Tarefa de teste", "description", LocalDate.now(),
                TaskStatus.PENDING,
                loggedUser);
        Task userTask2 = new Task(UUID.randomUUID(), "User id1 - Tarefa de teste2", "description", LocalDate.now(),
                TaskStatus.PENDING,
                loggedUser);
        Task task3 = new Task(UUID.randomUUID(), "Tarefa de teste3", "description", LocalDate.now(),
                TaskStatus.PENDING,
                anotherUser);

        List<Task> tasks = List.of(userTask1, userTask2, task3);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        SecurityContextHolder.setContext(securityContext); // setando o context falso pro teste

        when(this.taskRepository.findAll()).thenReturn(tasks);

        // não é preciso testar a variável interna ao método (userTasks)
        // em testes unitários, sempre é testado a saída ao método e os efeitos
        // colaterais.

        // ou seja, não importa COMO é feito, mas sim que FAÇA o que é esperado, que
        // nesse caso, é retornar o userTasks

        List<Task> result = this.taskService.getUserTasks();

        // valida o retorno, certificando de que é o mesmo
        assertEquals(userTask1.getUser().getId(), result.get(0).getUser().getId());
        assertEquals(userTask2.getUser().getId(), result.get(1).getUser().getId());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return an empty array if user dont have tasks")
    void getUserTasksCase2() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);
        List<Task> emptyUserTasks = List.of();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUser);
        SecurityContextHolder.setContext(securityContext);

        when(this.taskRepository.findAll()).thenReturn(emptyUserTasks);

        List<Task> result = this.taskService.getUserTasks();

        assertEquals(result.size(), emptyUserTasks.size());
    }

    @Test
    @DisplayName("Should create a task when everything is OK")
    void createTaskCase1() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(loggedUser);

        TaskDTO taskDTO = new TaskDTO("test", "test_desc", "25/04/2025", TaskStatus.PENDING);
        LocalDate dueDate = LocalDate.parse(taskDTO.due_date(), fmt);

        String uuid = "79846516-b43f-49d8-8316-d42aa7656be6";

        Task task = new Task(UUID.fromString(uuid), taskDTO.title(), taskDTO.description(), dueDate, taskDTO.status(),
                loggedUser);

        // sem isso, o retorno de createTask fica nulo, pois não dizemos o que deve ser
        // o retorno esperado
        // e como é um mock, ele não tem LÓGICA implementada. sendo assim, é
        // imprescindível indicar o que deve ser retornado, como o comportamneto será e
        // etc.
        when(this.taskRepository.save(any(Task.class))).thenReturn(task);

        this.taskService.createTask(taskDTO);

        // verificamos se foi chamado
        // na hora do create, não precisa ser IGUALDADE COMPLETA, basta que o objeto
        // salvo seja uma instância de task (assim como foi checado no mock)

        // pois a geração do id é sempre automatica, assim, o id sempre será diferente
        verify(this.taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw ADMCanNotCreateTaskException when ADM try to create task")
    void createTaskCase2() {
        User loggedUserADM = new User(1, "kaua", "123456789", UserRole.ADMIN, null);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUserADM);

        SecurityContextHolder.setContext(securityContext);

        TaskDTO taskDTO = new TaskDTO("test", "test_desc", "25/04/2025", TaskStatus.PENDING);

        ADMCanNotCreateTaskException thrown = Assertions.assertThrows(ADMCanNotCreateTaskException.class, () -> {
            this.taskService.createTask(taskDTO);
        });

        assertEquals("ADMs não podem criar tarefas.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should update a task when everything is OK")
    void updateTaskCase1() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUser);

        SecurityContextHolder.setContext(securityContext);

        TaskDTO taskDTO = new TaskDTO("test", "test_desc", "25/04/2025", TaskStatus.PENDING);
        LocalDate dueDate = LocalDate.parse(taskDTO.due_date(), fmt);

        String uuid = "79846516-b43f-49d8-8316-d42aa7656be6";

        Task task = new Task(UUID.fromString(uuid), taskDTO.title(), taskDTO.description(), dueDate, taskDTO.status(),
                loggedUser);

        // indicando que deve ser retornado a task acima.
        when(this.taskRepository.findById(uuid)).thenReturn(Optional.of(task));

        TaskDTO updatedData = new TaskDTO("task_updated", "updated_desc", "25/04/2025", TaskStatus.COMPLETED);

        task.setTitle(updatedData.title());
        task.setDescription(updatedData.description());
        task.setTask_status(updatedData.status());

        this.taskService.updateTask(uuid, updatedData);

        // aqui, a igualdade completa importa. o id já foi gerado e já eixste
        verify(this.taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException when task is not found")
    void updateTaskCase2() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUser);

        SecurityContextHolder.setContext(securityContext);

        TaskDTO updatedData = new TaskDTO("task_updated", "updated_desc", "25/04/2025", TaskStatus.COMPLETED);

        TaskNotFoundException thrown = Assertions.assertThrows(TaskNotFoundException.class, () -> {
            this.taskService.updateTask(null, updatedData);
        });

        assertEquals("Tarefa não encontrada.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw OtherUsersTasksCantBeUpdated when task to be updated is not a user task")
    void updateTaskCase3() {
        User loggedUser = new User(1, "kaua", "123456789", UserRole.USER, null);
        User anotherUser = new User(2, "kaua_2", "another_password", UserRole.USER, null);

        String uuid = "79846516-b43f-49d8-8316-d42aa7656be6";
        Task anotherUserTask = new Task(UUID.fromString(uuid), "title", "desc", LocalDate.now(), TaskStatus.COMPLETED,
                anotherUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedUser);

        SecurityContextHolder.setContext(securityContext);

        when(this.taskRepository.findById(uuid)).thenReturn(Optional.of(anotherUserTask));

        TaskDTO taskDTO = new TaskDTO("fake_dto", "fake_desc", "", TaskStatus.PENDING);

        OtherUserTasksCantBeUpdatedException thrown = Assertions
                .assertThrows(OtherUserTasksCantBeUpdatedException.class, () -> {
                    this.taskService.updateTask(uuid, taskDTO);
                });

        assertEquals("Tarefas de outros usuários não podem ser atualizadas.", thrown.getMessage());
    }
}
