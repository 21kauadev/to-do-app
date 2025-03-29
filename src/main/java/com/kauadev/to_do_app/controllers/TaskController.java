package com.kauadev.to_do_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kauadev.to_do_app.domain.task.Task;
import com.kauadev.to_do_app.domain.task.TaskDTO;
import com.kauadev.to_do_app.services.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = this.taskService.getAllTasks();

        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/userTasks")
    public ResponseEntity<List<Task>> getUserTasks() {
        List<Task> userTasks = this.taskService.getUserTasks();

        return ResponseEntity.ok().body(userTasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") String id) {
        Task task = this.taskService.getTask(id);

        return ResponseEntity.ok().body(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO data) {
        Task task = this.taskService.createTask(data);

        return ResponseEntity.ok().body(task);
    }

    @PatchMapping("/complete/{id}")
    public ResponseEntity<String> setTaskAsComplete(@PathVariable("id") String id) {
        this.taskService.setTaskAsCompleted(id);

        return ResponseEntity.ok().body("Tarefa concluída.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") String id, @RequestBody TaskDTO data) {
        Task task = this.taskService.updateTask(id, data);

        return ResponseEntity.ok().body(task);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") String id) {
        this.taskService.deleteTask(id);

        return ResponseEntity.ok().body(null);
    }
}
