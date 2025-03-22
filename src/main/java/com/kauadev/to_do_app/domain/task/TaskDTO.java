package com.kauadev.to_do_app.domain.task;

import java.time.LocalDate;

public record TaskDTO(String title, String description, LocalDate due_date, TaskStatus status, Integer user_id) {

}
