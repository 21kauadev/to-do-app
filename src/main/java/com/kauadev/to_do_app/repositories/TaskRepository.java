package com.kauadev.to_do_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kauadev.to_do_app.domain.task.Task;

public interface TaskRepository extends JpaRepository<Task, String> {

}
