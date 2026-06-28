package com.tdd.taskmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad principal Task (Tarea).
 * Mapeada a la tabla "tasks" mediante JPA/Hibernate ORM.
 *
 * Estados posibles: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
 */
@Entity
@Table(name = "tasks")
public class Task {

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Constructor por defecto (requerido por JPA)
    public Task() {
        this.status = Status.PENDING;
        this.priority = Priority.MEDIUM;
        this.createdAt = LocalDateTime.now();
    }

    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, Priority priority) {
        this(title, description);
        this.priority = priority;
    }

    // Metodo de dominio: marcar como completada
    public void complete() {
        if (this.status == Status.CANCELLED) {
            throw new IllegalStateException("No se puede completar una tarea cancelada.");
        }
        this.status = Status.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    // Metodo de dominio: cancelar tarea
    public void cancel() {
        if (this.status == Status.COMPLETED) {
            throw new IllegalStateException("No se puede cancelar una tarea ya completada.");
        }
        this.status = Status.CANCELLED;
    }

    // Metodo de dominio: iniciar tarea
    public void start() {
        if (this.status != Status.PENDING) {
            throw new IllegalStateException("Solo se pueden iniciar tareas en estado PENDING.");
        }
        this.status = Status.IN_PROGRESS;
    }

    // Metodo de dominio: verificar si esta vencida
    public boolean isOverdue() {
        if (dueDate == null || status == Status.COMPLETED || status == Status.CANCELLED) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', status=" + status + ", priority=" + priority + "}";
    }
}
