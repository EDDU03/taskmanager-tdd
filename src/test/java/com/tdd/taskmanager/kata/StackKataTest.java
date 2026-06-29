package com.tdd.taskmanager.kata;

import org.junit.jupiter.api.*;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KATA TDD #2: Stack (Pila)
 * ==========================
 * Implementacion de una Pila (Stack) usando TDD puro.
 * Cada prueba fue escrita ANTES de la implementacion correspondiente.
 *
 * Operaciones a implementar:
 *   push(item)  -> agrega elemento al tope
 *   pop()       -> retira y retorna el elemento del tope
 *   peek()      -> retorna el elemento del tope sin retirarlo
 *   isEmpty()   -> retorna true si la pila esta vacia
 *   size()      -> retorna el numero de elementos
 *
 * CICLO RED-GREEN-REFACTOR por iteracion:
 * ----------------------------------------
 * Iter 1: isEmpty() en pila nueva -> debe ser true
 * Iter 2: push() + size() -> size debe ser 1
 * Iter 3: push() + pop() -> debe retornar el elemento empujado
 * Iter 4: LIFO - ultimo en entrar, primero en salir
 * Iter 5: pop() en pila vacia -> EmptyStackException
 * Iter 6: peek() no modifica el tamano
 */
@DisplayName("Kata TDD #2 - Implementacion de Stack con TDD")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StackKataTest {

    private TDDStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new TDDStack<>();
    }

    // Iteracion 1: isEmpty
    @Test
    @Order(1)
    @DisplayName("[Iter 1 - RED->GREEN] Pila nueva debe estar vacia")
    void pilaNuevaDebeEstarVacia() {
        assertTrue(stack.isEmpty(), "La pila recien creada debe estar vacia");
        assertEquals(0, stack.size(), "Tamano inicial debe ser 0");
    }

    // Iteracion 2: push y size
    @Test
    @Order(2)
    @DisplayName("[Iter 2 - RED->GREEN] Despues de push, la pila no debe estar vacia")
    void despuesDePushPilaNoEstaVacia() {
        stack.push(42);

        assertFalse(stack.isEmpty());
        assertEquals(1, stack.size());
    }

    // Iteracion 3: push y pop
    @Test
    @Order(3)
    @DisplayName("[Iter 3 - RED->GREEN] Pop debe retornar el elemento empujado")
    void popDebeRetornarElementoEmpujado() {
        stack.push(10);

        Integer resultado = stack.pop();

        assertEquals(10, resultado);
        assertTrue(stack.isEmpty(), "Pila debe estar vacia despues de pop");
    }

    // Iteracion 4: LIFO
    @Test
    @Order(4)
    @DisplayName("[Iter 4 - RED->GREEN] Debe respetar el orden LIFO (ultimo en entrar, primero en salir)")
    void debeRespetarOrdenLIFO() {
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop(), "Primero debe salir el ultimo en entrar");
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop(), "Ultimo en salir debe ser el primero en entrar");
        assertTrue(stack.isEmpty());
    }

    // Iteracion 5: excepcion en pila vacia
    @Test
    @Order(5)
    @DisplayName("[Iter 5 - RED->GREEN] Pop en pila vacia debe lanzar EmptyStackException")
    void popEnPilaVaciaLanzaExcepcion() {
        assertThrows(
            EmptyStackException.class,
            () -> stack.pop(),
            "Pop en pila vacia debe lanzar EmptyStackException"
        );
    }

    // Iteracion 6: peek
    @Test
    @Order(6)
    @DisplayName("[Iter 6 - RED->GREEN] Peek debe retornar elemento sin removerlo")
    void peekDebeRetornarElementoSinRemoverlo() {
        stack.push(99);

        Integer tope = stack.peek();

        assertEquals(99, tope);
        assertEquals(1, stack.size(), "Size no debe cambiar despues de peek");
        assertFalse(stack.isEmpty(), "Pila no debe vaciarse despues de peek");
    }

    @Test
    @Order(7)
    @DisplayName("[Iter 6 - RED->GREEN] Peek en pila vacia debe lanzar EmptyStackException")
    void peekEnPilaVaciaLanzaExcepcion() {
        assertThrows(EmptyStackException.class, () -> stack.peek());
    }

    // Iteracion 7: REFACTOR - multiples operaciones combinadas
    @Test
    @Order(8)
    @DisplayName("[REFACTOR] Secuencia compleja de operaciones debe mantener consistencia")
    void secuenciaComplejaDebe MantenerConsistencia() {
        // Push 5 elementos
        for (int i = 1; i <= 5; i++) {
            stack.push(i * 10);
        }
        assertEquals(5, stack.size());

        // Pop 2 -> deben ser 50 y 40
        assertEquals(50, stack.pop());
        assertEquals(40, stack.pop());
        assertEquals(3, stack.size());

        // Peek -> debe ser 30 sin cambiar size
        assertEquals(30, stack.peek());
        assertEquals(3, stack.size());

        // Empujar mas
        stack.push(99);
        assertEquals(4, stack.size());
        assertEquals(99, stack.peek());
    }

    // =========================================================
    // IMPLEMENTACION DE TDDSTACK
    // Construida iteracion a iteracion siguiendo las pruebas
    // =========================================================

    /**
     * Implementacion de pila generica siguiendo TDD.
     * Internamente usa un array dinamico.
     *
     * @param <T> tipo de elementos almacenados
     */
    static class TDDStack<T> {
        private Object[] elements;
        private int size;
        private static final int INITIAL_CAPACITY = 10;

        public TDDStack() {
            elements = new Object[INITIAL_CAPACITY];
            size = 0;
        }

        public void push(T item) {
            ensureCapacity();
            elements[size++] = item;
        }

        @SuppressWarnings("unchecked")
        public T pop() {
            if (isEmpty()) throw new EmptyStackException();
            T item = (T) elements[--size];
            elements[size] = null; // ayuda al GC
            return item;
        }

        @SuppressWarnings("unchecked")
        public T peek() {
            if (isEmpty()) throw new EmptyStackException();
            return (T) elements[size - 1];
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        private void ensureCapacity() {
            if (size == elements.length) {
                Object[] newElements = new Object[elements.length * 2];
                System.arraycopy(elements, 0, newElements, 0, size);
                elements = newElements;
            }
        }
    }
}
