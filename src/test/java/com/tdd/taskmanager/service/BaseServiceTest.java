package com.tdd.taskmanager.service;

import com.tdd.taskmanager.repository.JPAUtil;
import com.tdd.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Clase base para todas las pruebas que requieren acceso a la base de datos.
 * Inicializa el EntityManagerFactory con la unidad de persistencia de pruebas.
 *
 * CICLO TDD:
 * - @BeforeAll: Inicializa ORM una sola vez para toda la suite.
 * - @BeforeEach: Limpia la base de datos antes de cada prueba (aislamiento).
 * - @AfterAll:  Cierra el ORM al terminar todas las pruebas.
 */
public abstract class BaseServiceTest {

    protected static EntityManagerFactory emf;
    protected static TaskRepository taskRepository;
    protected static TaskService taskService;

    @BeforeAll
    static void setUpORM() {
        // Usar unidad de persistencia de PRUEBAS (H2 en memoria)
        JPAUtil.initWithPU("taskmanager-test-pu");
        emf = JPAUtil.getEntityManagerFactory();
        taskRepository = new TaskRepository(emf);
        taskService = new TaskService(taskRepository);
    }

    @BeforeEach
    void cleanDatabase() {
        // Limpiar la base de datos antes de cada prueba para aislamiento
        taskRepository.deleteAll();
    }

    @AfterAll
    static void tearDownORM() {
        JPAUtil.close();
    }
}
