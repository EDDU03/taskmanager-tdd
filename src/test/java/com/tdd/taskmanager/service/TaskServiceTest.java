package com.tdd.taskmanager.service;

import com.tdd.taskmanager.model.Task;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Pruebas Unitarias para TaskService.
 *
 * METODOLOGIA TDD - Ciclo Red-Green-Refactor:
 * ============================================
 * RED   -> Se escribe la prueba ANTES de implementar el codigo.
 *           La prueba FALLA porque la funcionalidad no existe aun.
 * GREEN -> Se implementa el MINIMO codigo necesario para que la prueba pase.
 * REFACTOR -> Se mejora el codigo manteniendo todas las pruebas en verde.
 *
 * Cada metodo de prueba esta anotado indicando en que fase TDD fue escrito.
 */
@DisplayName("TaskService - Suite de Pruebas TDD")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskServiceTest extends BaseServiceTest {

    // =========================================================
    // ITERACION 1: Creacion de Tareas (Kata TDD #1)
    // Fase RED: Estas pruebas se escribieron antes de implementar createTask()
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #1 - Creacion de Tareas")
    class CreacionDeTareas {

        @Test
        @Order(1)
        @DisplayName("[RED->GREEN] Debe crear tarea con titulo y descripcion validos")
        void debeCrearTareaConDatosValidos() {
            // ARRANGE (Given)
            String titulo = "Implementar login de usuario";
            String descripcion = "Crear formulario de autenticacion con JWT";

            // ACT (When)
            Task tarea = taskService.createTask(titulo, descripcion);

            // ASSERT (Then)
            assertNotNull(tarea, "La tarea creada no debe ser null");
            assertNotNull(tarea.getId(), "La tarea debe tener un ID generado por la BD");
            assertEquals(titulo, tarea.getTitle(), "El titulo debe coincidir");
            assertEquals(descripcion, tarea.getDescription(), "La descripcion debe coincidir");
            assertEquals(Task.Status.PENDING, tarea.getStatus(), "Estado inicial debe ser PENDING");
            assertEquals(Task.Priority.MEDIUM, tarea.getPriority(), "Prioridad inicial debe ser MEDIUM");
        }

        @Test
        @Order(2)
        @DisplayName("[RED->GREEN] Debe crear tarea con prioridad HIGH")
        void debeCrearTareaConPrioridadAlta() {
            // ARRANGE
            String titulo = "Corregir bug critico en produccion";

            // ACT
            Task tarea = taskService.createTask(titulo, "Error en pago", Task.Priority.HIGH);

            // ASSERT
            assertEquals(Task.Priority.HIGH, tarea.getPriority());
            assertNotNull(tarea.getId());
        }

        @ParameterizedTest
        @Order(3)
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("[RED->GREEN] Debe rechazar titulo nulo, vacio o solo espacios")
        void debeRechazarTituloInvalido(String tituloInvalido) {
            // ACT & ASSERT
            assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(tituloInvalido, "descripcion"),
                "Debe lanzar IllegalArgumentException para titulo: '" + tituloInvalido + "'"
            );
        }

        @Test
        @Order(4)
        @DisplayName("[RED->GREEN] Debe rechazar titulo con mas de 200 caracteres")
        void debeRechazarTituloDemasiadoLargo() {
            // ARRANGE
            String tituloLargo = "A".repeat(201); // 201 caracteres

            // ACT & ASSERT
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(tituloLargo, "descripcion")
            );
            assertTrue(ex.getMessage().contains("200"));
        }

        @Test
        @Order(5)
        @DisplayName("[REFACTOR] Debe recortar espacios del titulo al crear")
        void debeRecortarEspaciosDelTitulo() {
            // ARRANGE
            String tituloConEspacios = "  Tarea con espacios  ";

            // ACT
            Task tarea = taskService.createTask(tituloConEspacios, "desc");

            // ASSERT
            assertEquals("Tarea con espacios", tarea.getTitle(),
                "El titulo debe estar recortado (trim)");
        }
    }

    // =========================================================
    // ITERACION 2: Ciclo de Vida de la Tarea (Kata TDD #2)
    // Fase RED: Pruebas del ciclo de estados antes de implementar
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #2 - Ciclo de Vida (Estados)")
    class CicloDeVida {

        @Test
        @Order(10)
        @DisplayName("[RED->GREEN] Debe iniciar una tarea PENDING -> IN_PROGRESS")
        void debeIniciarTarea() {
            // ARRANGE
            Task tarea = taskService.createTask("Analisis de requisitos", "Sprint 1");

            // ACT
            Task iniciada = taskService.startTask(tarea.getId());

            // ASSERT
            assertEquals(Task.Status.IN_PROGRESS, iniciada.getStatus());
        }

        @Test
        @Order(11)
        @DisplayName("[RED->GREEN] Debe completar una tarea IN_PROGRESS -> COMPLETED")
        void debeCompletarTarea() {
            // ARRANGE
            Task tarea = taskService.createTask("Desarrollar API REST", "Endpoints CRUD");
            taskService.startTask(tarea.getId());

            // ACT
            Task completada = taskService.completeTask(tarea.getId());

            // ASSERT
            assertEquals(Task.Status.COMPLETED, completada.getStatus());
            assertNotNull(completada.getCompletedAt(), "Debe registrar la fecha de completado");
        }

        @Test
        @Order(12)
        @DisplayName("[RED->GREEN] Debe completar tarea PENDING directamente")
        void debeCompletarTareaPendiente() {
            // ARRANGE
            Task tarea = taskService.createTask("Reunion de equipo", "Daily stand-up");

            // ACT
            Task completada = taskService.completeTask(tarea.getId());

            // ASSERT
            assertEquals(Task.Status.COMPLETED, completada.getStatus());
        }

        @Test
        @Order(13)
        @DisplayName("[RED->GREEN] Debe cancelar una tarea")
        void debeCancelarTarea() {
            // ARRANGE
            Task tarea = taskService.createTask("Feature deprecada", "Ya no es necesaria");

            // ACT
            Task cancelada = taskService.cancelTask(tarea.getId());

            // ASSERT
            assertEquals(Task.Status.CANCELLED, cancelada.getStatus());
        }

        @Test
        @Order(14)
        @DisplayName("[RED->GREEN] No debe poder completar una tarea CANCELADA")
        void noDebeCompletarTareaCancelada() {
            // ARRANGE
            Task tarea = taskService.createTask("Tarea obsoleta", "Cancelada");
            taskService.cancelTask(tarea.getId());

            // ACT & ASSERT
            assertThrows(
                IllegalStateException.class,
                () -> taskService.completeTask(tarea.getId()),
                "Debe lanzar excepcion al intentar completar tarea cancelada"
            );
        }

        @Test
        @Order(15)
        @DisplayName("[RED->GREEN] No debe poder cancelar una tarea COMPLETADA")
        void noDebeCancelarTareaCompletada() {
            // ARRANGE
            Task tarea = taskService.createTask("Tarea terminada", "Ya completada");
            taskService.completeTask(tarea.getId());

            // ACT & ASSERT
            assertThrows(
                IllegalStateException.class,
                () -> taskService.cancelTask(tarea.getId()),
                "Debe lanzar excepcion al intentar cancelar tarea completada"
            );
        }

        @Test
        @Order(16)
        @DisplayName("[RED->GREEN] No debe iniciar tarea que no esta en PENDING")
        void noDebeIniciarTareaEnEstadoIncorrecto() {
            // ARRANGE
            Task tarea = taskService.createTask("En progreso", "Ya iniciada");
            taskService.startTask(tarea.getId());

            // ACT & ASSERT - intentar iniciar dos veces
            assertThrows(
                IllegalStateException.class,
                () -> taskService.startTask(tarea.getId())
            );
        }
    }

    // =========================================================
    // ITERACION 3: Consultas y Busqueda (Kata TDD #3)
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #3 - Consultas y Filtros")
    class ConsultasYFiltros {

        @Test
        @Order(20)
        @DisplayName("[RED->GREEN] Debe obtener todas las tareas")
        void debeObtenerTodasLasTareas() {
            // ARRANGE
            taskService.createTask("Tarea A", "desc A");
            taskService.createTask("Tarea B", "desc B");
            taskService.createTask("Tarea C", "desc C");

            // ACT
            List<Task> tareas = taskService.getAllTasks();

            // ASSERT
            assertEquals(3, tareas.size(), "Debe haber exactamente 3 tareas");
        }

        @Test
        @Order(21)
        @DisplayName("[RED->GREEN] Debe filtrar tareas por estado PENDING")
        void debeFiltrarTareasPendientes() {
            // ARRANGE
            taskService.createTask("Pendiente 1", "");
            taskService.createTask("Pendiente 2", "");
            Task completada = taskService.createTask("Completada", "");
            taskService.completeTask(completada.getId());

            // ACT
            List<Task> pendientes = taskService.getPendingTasks();

            // ASSERT
            assertEquals(2, pendientes.size());
            assertTrue(pendientes.stream().allMatch(t -> t.getStatus() == Task.Status.PENDING));
        }

        @Test
        @Order(22)
        @DisplayName("[RED->GREEN] Debe buscar tareas por palabra clave en el titulo")
        void debeBuscarTareasPorTitulo() {
            // ARRANGE
            taskService.createTask("Implementar login", "auth");
            taskService.createTask("Implementar registro", "sign up");
            taskService.createTask("Corregir bug en pagos", "bug fix");

            // ACT
            List<Task> resultados = taskService.searchByTitle("implementar");

            // ASSERT
            assertEquals(2, resultados.size(), "Solo las tareas con 'implementar' en el titulo");
            assertTrue(resultados.stream()
                .allMatch(t -> t.getTitle().toLowerCase().contains("implementar")));
        }

        @Test
        @Order(23)
        @DisplayName("[RED->GREEN] Busqueda debe ser insensible a mayusculas/minusculas")
        void busquedaDebeSerInsensibleAMayusculas() {
            // ARRANGE
            taskService.createTask("IMPLEMENTAR CACHE", "Redis");

            // ACT
            List<Task> r1 = taskService.searchByTitle("implementar");
            List<Task> r2 = taskService.searchByTitle("IMPLEMENTAR");
            List<Task> r3 = taskService.searchByTitle("Implementar");

            // ASSERT
            assertEquals(1, r1.size());
            assertEquals(1, r2.size());
            assertEquals(1, r3.size());
        }

        @ParameterizedTest
        @Order(24)
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("[RED->GREEN] Busqueda con keyword invalido debe lanzar excepcion")
        void busquedaConKeywordInvalidoLanzaExcepcion(String keyword) {
            assertThrows(
                IllegalArgumentException.class,
                () -> taskService.searchByTitle(keyword)
            );
        }

        @Test
        @Order(25)
        @DisplayName("[RED->GREEN] Debe lanzar excepcion al buscar ID inexistente")
        void debeLanzarExcepcionParaIdInexistente() {
            // ACT & ASSERT
            assertThrows(
                RuntimeException.class,
                () -> taskService.getTaskById(9999L),
                "Debe lanzar excepcion para ID que no existe"
            );
        }
    }

    // =========================================================
    // ITERACION 4: Fechas Limite y Vencimiento (Kata TDD #4)
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #4 - Fechas Limite")
    class FechasLimite {

        @Test
        @Order(30)
        @DisplayName("[RED->GREEN] Debe crear tarea con fecha limite futura")
        void debeCrearTareaConFechaFutura() {
            // ARRANGE
            LocalDate manana = LocalDate.now().plusDays(1);

            // ACT
            Task tarea = taskService.createTaskWithDueDate("Sprint review", "Q4", manana);

            // ASSERT
            assertNotNull(tarea.getId());
            assertEquals(manana, tarea.getDueDate());
        }

        @Test
        @Order(31)
        @DisplayName("[RED->GREEN] No debe crear tarea con fecha limite en el pasado")
        void noDebeCrearTareaConFechaPasada() {
            // ARRANGE
            LocalDate ayer = LocalDate.now().minusDays(1);

            // ACT & ASSERT
            assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTaskWithDueDate("Tarea atrasada", "desc", ayer)
            );
        }

        @Test
        @Order(32)
        @DisplayName("[RED->GREEN] Tarea sin fecha de vencimiento no debe estar vencida")
        void tareasSinFechaNoEstaVencida() {
            // ARRANGE
            Task tarea = taskService.createTask("Sin deadline", "desc");

            // ASSERT
            assertFalse(tarea.isOverdue(), "Tarea sin fecha no debe estar vencida");
        }
    }

    // =========================================================
    // ITERACION 5: Estadisticas y Resumen (Kata TDD #5)
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #5 - Estadisticas del Sistema")
    class Estadisticas {

        @Test
        @Order(40)
        @DisplayName("[RED->GREEN] Resumen debe reflejar el estado actual de las tareas")
        void resumenDebeReflejarEstadoActual() {
            // ARRANGE
            taskService.createTask("Pendiente 1", "");
            taskService.createTask("Pendiente 2", "");

            Task inProgress = taskService.createTask("En progreso", "");
            taskService.startTask(inProgress.getId());

            Task completed = taskService.createTask("Completada", "");
            taskService.completeTask(completed.getId());

            Task cancelled = taskService.createTask("Cancelada", "");
            taskService.cancelTask(cancelled.getId());

            // ACT
            TaskService.TaskSummary resumen = taskService.getSummary();

            // ASSERT
            assertEquals(2, resumen.pending());
            assertEquals(1, resumen.inProgress());
            assertEquals(1, resumen.completed());
            assertEquals(1, resumen.cancelled());
            assertEquals(5, resumen.total());
        }

        @Test
        @Order(41)
        @DisplayName("[RED->GREEN] Resumen con BD vacia debe tener todos los valores en 0")
        void resumenVacioDeben SerCero() {
            // ACT
            TaskService.TaskSummary resumen = taskService.getSummary();

            // ASSERT
            assertAll("Todos los contadores deben ser 0",
                () -> assertEquals(0, resumen.pending()),
                () -> assertEquals(0, resumen.inProgress()),
                () -> assertEquals(0, resumen.completed()),
                () -> assertEquals(0, resumen.cancelled()),
                () -> assertEquals(0, resumen.total())
            );
        }
    }

    // =========================================================
    // ITERACION 6: Actualizacion de Tareas (Kata TDD #6)
    // =========================================================

    @Nested
    @DisplayName("Kata TDD #6 - Actualizacion de Tareas")
    class ActualizacionDeTareas {

        @Test
        @Order(50)
        @DisplayName("[RED->GREEN] Debe actualizar el titulo de una tarea")
        void debeActualizarTitulo() {
            // ARRANGE
            Task tarea = taskService.createTask("Titulo original", "desc");
            String nuevoTitulo = "Titulo actualizado";

            // ACT
            Task actualizada = taskService.updateTitle(tarea.getId(), nuevoTitulo);

            // ASSERT
            assertEquals(nuevoTitulo, actualizada.getTitle());
            assertEquals(tarea.getId(), actualizada.getId(), "El ID debe mantenerse igual");
        }

        @Test
        @Order(51)
        @DisplayName("[RED->GREEN] Debe actualizar la prioridad a HIGH")
        void debeActualizarPrioridad() {
            // ARRANGE
            Task tarea = taskService.createTask("Tarea urgente", "desc");
            assertEquals(Task.Priority.MEDIUM, tarea.getPriority());

            // ACT
            Task actualizada = taskService.updatePriority(tarea.getId(), Task.Priority.HIGH);

            // ASSERT
            assertEquals(Task.Priority.HIGH, actualizada.getPriority());
        }

        @Test
        @Order(52)
        @DisplayName("[RED->GREEN] Debe eliminar una tarea existente")
        void debeEliminarTarea() {
            // ARRANGE
            Task tarea = taskService.createTask("Para eliminar", "desc");
            Long id = tarea.getId();

            // ACT
            boolean eliminada = taskService.deleteTask(id);

            // ASSERT
            assertTrue(eliminada);
            assertThrows(RuntimeException.class, () -> taskService.getTaskById(id),
                "La tarea eliminada no debe encontrarse");
        }

        @Test
        @Order(53)
        @DisplayName("[RED->GREEN] Eliminar tarea inexistente debe lanzar excepcion")
        void eliminarTareaInexistenteLanzaExcepcion() {
            assertThrows(RuntimeException.class,
                () -> taskService.deleteTask(99999L));
        }
    }
}
