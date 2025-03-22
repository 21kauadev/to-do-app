package com.kauadev.to_do_app.domain.task;

import java.time.LocalDate;

import com.kauadev.to_do_app.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;
    private LocalDate due_date;
    private TaskStatus task_status;

    // relação de um-um. task precisa do id do usuario que a criou
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Task(String title, String description, LocalDate due_date, TaskStatus taskStatus) {
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.task_status = taskStatus;
    }

}
