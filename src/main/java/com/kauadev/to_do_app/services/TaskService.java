package com.kauadev.to_do_app.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.domain.task.TaskDTO;
import com.kauadev.to_do_app.domain.user.User;
import com.kauadev.to_do_app.repositories.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Task> getAllTasks() {
        return this.taskRepository.findAll();
    }

    public Task getTask(String id) {
        Optional<Task> task = this.taskRepository.findById(id);

        return task.get();
    }

    // getUserTasks

    public Task createTask(TaskDTO data) {

        // pega o usuario autenticado e extrai o id
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        LocalDate dueDate = LocalDate.parse(data.due_date(), fmt);

        // se o id ta diferente do id que ta no token, erro de autenticação!

        // aqui, o user passado é o user que é extraido com base no contexto de
        // autenticação do spring security.
        Task task = new Task(data.title(), data.description(), dueDate, data.status(), user);

        return this.taskRepository.save(task);
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
