package com.kauadev.to_do_app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.domain.task.TaskDTO;
import com.kauadev.to_do_app.domain.task.TaskStatus;
import com.kauadev.to_do_app.domain.task.exceptions.OtherUserTasksCantBeDeletedException;
import com.kauadev.to_do_app.domain.task.exceptions.OtherUserTasksCantBeUpdatedException;
import com.kauadev.to_do_app.domain.task.exceptions.TaskNotFoundException;
import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.domain.user.exceptions.ADMCanNotCreateTaskException;
import com.kauadev.to_do_app.domain.user.exceptions.UserCanNotSeeOtherUsersTasks;
import com.kauadev.to_do_app.repositories.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Task> getAllTasks() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new UserCanNotSeeOtherUsersTasks();
        }

        return this.taskRepository.findAll();
    }

    public Task getTask(String id) {
        Task task = this.taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        return task;
    }

    public List<Task> getUserTasks() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Task> tasks = this.taskRepository.findAll();
        // filtra os usuarios que tem o id igual ao campo user_id na task.
        List<Task> userTasks = tasks.stream().filter((task) -> task.getUser().getId() == user.getId()).toList();

        return userTasks;
    }

    public Task createTask(TaskDTO data) {

        // pega o usuario autenticado e extrai o id
        // se o id ta diferente do id que ta no token, erro de autenticação!
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // se for adm, nao pode criar. adm não é um usuario comum que cria tarefas.
        // seu papel é GERENCIAR.
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ADMCanNotCreateTaskException();
        }

        LocalDate dueDate = LocalDate.parse(data.due_date(), fmt);
        // aqui, o user passado é o user que é extraido com base no contexto de
        // autenticação do spring security.
        Task task = new Task(data.title(), data.description(), dueDate, data.status(), user);

        return this.taskRepository.save(task);
    }

    public Task updateTask(String id, TaskDTO data) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Task task = this.taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && task.getUser().getId() != user.getId()) {
            throw new OtherUserTasksCantBeUpdatedException();
        }

        task.setTitle(data.title());
        task.setDescription(data.description());
        task.setTask_status(data.status());

        return this.taskRepository.save(task);
    }

    public Task setTaskAsCompleted(String id) {
        Task task = this.taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        task.setTask_status(TaskStatus.valueOf("COMPLETED"));

        return this.taskRepository.save(task);
    }

    public String deleteTask(String id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Task task = this.taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

        // se o usuário não for um ADM e mesmo assim tentar deletar uma tarefa que não é
        // DELE, lança um erro
        if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && task.getUser().getId() != user.getId()) {
            throw new OtherUserTasksCantBeDeletedException();
        }

        this.taskRepository.delete(task);

        return null;
    }
}
