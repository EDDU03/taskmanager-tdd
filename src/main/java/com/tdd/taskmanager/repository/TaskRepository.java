package com.tdd.taskmanager.repository;

import com.tdd.taskmanager.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Tareas usando JPA/Hibernate ORM.
 * Implementa operaciones CRUD y consultas especializadas.
 *
 * Patron: Repository (separa logica de negocio del acceso a datos).
 */
public class TaskRepository {

    private final EntityManagerFactory emf;

    public TaskRepository() {
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public TaskRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Guarda o actualiza una tarea en la base de datos.
     * Si la tarea no tiene ID, la inserta; si tiene ID, la actualiza.
     */
    public Task save(Task task) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Task result;
            if (task.getId() == null) {
                em.persist(task);
                result = task;
            } else {
                result = em.merge(task);
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al guardar la tarea: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Busca una tarea por su ID.
     */
    public Optional<Task> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Task task = em.find(Task.class, id);
            return Optional.ofNullable(task);
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene todas las tareas.
     */
    public List<Task> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t", Task.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca tareas por estado.
     */
    public List<Task> findByStatus(Task.Status status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Task> query = em.createQuery(
                "SELECT t FROM Task t WHERE t.status = :status ORDER BY t.createdAt DESC",
                Task.class
            );
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca tareas por prioridad.
     */
    public List<Task> findByPriority(Task.Priority priority) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Task> query = em.createQuery(
                "SELECT t FROM Task t WHERE t.priority = :priority ORDER BY t.createdAt DESC",
                Task.class
            );
            query.setParameter("priority", priority);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Busca tareas cuyo titulo contenga el texto dado (busqueda parcial).
     */
    public List<Task> findByTitleContaining(String keyword) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Task> query = em.createQuery(
                "SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(:keyword)",
                Task.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Cuenta las tareas por estado.
     */
    public long countByStatus(Task.Status status) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(t) FROM Task t WHERE t.status = :status",
                Long.class
            );
            query.setParameter("status", status);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Elimina una tarea por su ID.
     */
    public boolean deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Task task = em.find(Task.class, id);
            if (task == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(task);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar tarea: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Elimina todas las tareas (util para pruebas).
     */
    public void deleteAll() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Task t").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al eliminar todas las tareas", e);
        } finally {
            em.close();
        }
    }
}
