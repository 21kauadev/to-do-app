package com.kauadev.to_do_app.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.repositories.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return this.taskRepository.findAll();
    }

    public Task getTask(String id) {
        Optional<Task> task = this.taskRepository.findById(id);

        return task.get();
    }

    public Task createTask(Task newTask) {
        return this.taskRepository.save(newTask);
    }

    public Task updateTask(String id, Task newTask) {
        Optional<Task> task = this.taskRepository.findById(id);

        task.get().setTitle(newTask.getTitle());
        task.get().setDescription(newTask.getDescription());
        task.get().setTask_status(newTask.getTask_status());

        return this.taskRepository.save(task.get());
    }

    public String deleteTask(String id) {
        Optional<Task> task = this.taskRepository.findById(id);

        this.taskRepository.delete(task.get());

        return null;
    }
}
