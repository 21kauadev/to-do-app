package com.kauadev.to_do_app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.domain.task.TaskDTO;
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
        Optional<Task> task = this.taskRepository.findById(id);

        return task.get();
    }

    // getUserTasks

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

        return task;
    }

    public Task updateTask(String id, TaskDTO data) {
        Optional<Task> task = this.taskRepository.findById(id);

        task.get().setTitle(data.title());
        task.get().setDescription(data.description());
        task.get().setTask_status(data.status());

        return this.taskRepository.save(task.get());
    }

    public String deleteTask(String id) {
        Optional<Task> task = this.taskRepository.findById(id);

        this.taskRepository.delete(task.get());

        return null;
    }
}
