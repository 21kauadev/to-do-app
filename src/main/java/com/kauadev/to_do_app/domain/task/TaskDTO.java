package com.kauadev.to_do_app.domain.task;

public record TaskDTO(String title, String description, String due_date, TaskStatus status, Integer user_id) {

}
