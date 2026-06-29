package com.tdd.taskmanager.service;

import com.tdd.taskmanager.model.Task;
import com.tdd.taskmanager.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestion de Tareas.
 * Aplica reglas de negocio y coordina operaciones sobre el repositorio.
 *
 * Sigue el patron Service Layer para separar logica de negocio
 * del acceso a datos (repositorio) y la interfaz de usuario.
 */
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Crea una nueva tarea validando que el titulo no este vacio.
     */
    public Task createTask(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo de la tarea no puede estar vacio.");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("El titulo no puede superar los 200 caracteres.");
        }
        Task task = new Task(title.trim(), description);
        return taskRepository.save(task);
    }

    /**
     * Crea una tarea con prioridad especifica.
     */
    public Task createTask(String title, String description, Task.Priority priority) {
        Task task = createTask(title, description);
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    /**
     * Crea una tarea con fecha limite.
     */
    public Task createTaskWithDueDate(String title, String description, LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha limite no puede ser en el pasado.");
        }
        Task task = createTask(title, description);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    /**
     * Busca una tarea por ID, lanzando excepcion si no existe.
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tarea con ID " + id + " no encontrada."));
    }

    /**
     * Obtiene todas las tareas existentes.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Marca una tarea como completada.
     */
    public Task completeTask(Long id) {
        Task task = getTaskById(id);
        task.complete();
        return taskRepository.save(task);
    }

    /**
     * Cancela una tarea.
     */
    public Task cancelTask(Long id) {
        Task task = getTaskById(id);
        task.cancel();
        return taskRepository.save(task);
    }

    /**
     * Inicia una tarea (cambia de PENDING a IN_PROGRESS).
     */
    public Task startTask(Long id) {
        Task task = getTaskById(id);
        task.start();
        return taskRepository.save(task);
    }

    /**
     * Actualiza el titulo de una tarea.
     */
    public Task updateTitle(Long id, String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo titulo no puede estar vacio.");
        }
        Task task = getTaskById(id);
        task.setTitle(newTitle.trim());
        return taskRepository.save(task);
    }

    /**
     * Actualiza la prioridad de una tarea.
     */
    public Task updatePriority(Long id, Task.Priority priority) {
        Task task = getTaskById(id);
        task.setPriority(priority);
        return taskRepository.save(task);
    }

    /**
     * Obtiene tareas pendientes ordenadas por prioridad.
     */
    public List<Task> getPendingTasks() {
        return taskRepository.findByStatus(Task.Status.PENDING);
    }

    /**
     * Obtiene tareas en progreso.
     */
    public List<Task> getInProgressTasks() {
        return taskRepository.findByStatus(Task.Status.IN_PROGRESS);
    }

    /**
     * Obtiene tareas completadas.
     */
    public List<Task> getCompletedTasks() {
        return taskRepository.findByStatus(Task.Status.COMPLETED);
    }

    /**
     * Busca tareas por palabra clave en el titulo.
     */
    public List<Task> searchByTitle(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("La palabra clave de busqueda no puede estar vacia.");
        }
        return taskRepository.findByTitleContaining(keyword.trim());
    }

    /**
     * Elimina una tarea por ID.
     */
    public boolean deleteTask(Long id) {
        // Verificar que existe antes de eliminar
        getTaskById(id);
        return taskRepository.deleteById(id);
    }

    /**
     * Genera un resumen estadistico de las tareas.
     */
    public TaskSummary getSummary() {
        long pending = taskRepository.countByStatus(Task.Status.PENDING);
        long inProgress = taskRepository.countByStatus(Task.Status.IN_PROGRESS);
        long completed = taskRepository.countByStatus(Task.Status.COMPLETED);
        long cancelled = taskRepository.countByStatus(Task.Status.CANCELLED);
        return new TaskSummary(pending, inProgress, completed, cancelled);
    }

    /**
     * Clase de datos para el resumen estadistico.
     */
    public record TaskSummary(long pending, long inProgress, long completed, long cancelled) {
        public long total() {
            return pending + inProgress + completed + cancelled;
        }
    }
}
