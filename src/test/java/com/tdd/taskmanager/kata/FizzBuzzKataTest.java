package com.tdd.taskmanager.kata;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KATA TDD #1: FizzBuzz
 * =======================
 * Problema clasico para demostrar el ciclo Red-Green-Refactor de TDD.
 *
 * Reglas:
 *   - Si el numero es multiplo de 3 -> "Fizz"
 *   - Si el numero es multiplo de 5 -> "Buzz"
 *   - Si es multiplo de ambos (15)   -> "FizzBuzz"
 *   - En cualquier otro caso         -> el numero en String
 *
 * ITERACIONES DEL KATA:
 * Iteracion 1 (RED):   Escribir prueba para numero normal -> FALLA (no hay codigo)
 * Iteracion 1 (GREEN): Retornar String.valueOf(n) -> PASA
 * Iteracion 2 (RED):   Probar multiplo de 3 -> FALLA
 * Iteracion 2 (GREEN): Agregar if (n%3==0) return "Fizz" -> PASA
 * Iteracion 3 (RED):   Probar multiplo de 5 -> FALLA
 * Iteracion 3 (GREEN): Agregar if (n%5==0) return "Buzz" -> PASA
 * Iteracion 4 (RED):   Probar multiplo de 15 -> FALLA
 * Iteracion 4 (GREEN): Agregar if (n%15==0) check primero -> PASA
 * Iteracion 5 (REFACTOR): Limpiar codigo sin romper pruebas
 */
@DisplayName("Kata TDD #1 - FizzBuzz")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FizzBuzzKataTest {

    private FizzBuzz fizzBuzz;

    @BeforeEach
    void setUp() {
        fizzBuzz = new FizzBuzz();
    }

    // --- ITERACION 1: numero normal ---
    @Test
    @Order(1)
    @DisplayName("[Iteracion 1 - RED->GREEN] Numero no multiplo debe retornar el numero como String")
    void numeroNormalDebeRetornarSuValor() {
        assertEquals("1", fizzBuzz.evaluate(1));
        assertEquals("2", fizzBuzz.evaluate(2));
        assertEquals("4", fizzBuzz.evaluate(4));
        assertEquals("7", fizzBuzz.evaluate(7));
    }

    // --- ITERACION 2: multiplo de 3 ---
    @Test
    @Order(2)
    @DisplayName("[Iteracion 2 - RED->GREEN] Multiplo de 3 debe retornar 'Fizz'")
    void multiploTresDebe RetornarFizz() {
        assertEquals("Fizz", fizzBuzz.evaluate(3));
        assertEquals("Fizz", fizzBuzz.evaluate(6));
        assertEquals("Fizz", fizzBuzz.evaluate(9));
        assertEquals("Fizz", fizzBuzz.evaluate(33));
    }

    // --- ITERACION 3: multiplo de 5 ---
    @Test
    @Order(3)
    @DisplayName("[Iteracion 3 - RED->GREEN] Multiplo de 5 debe retornar 'Buzz'")
    void multiploCincoDebe RetornarBuzz() {
        assertEquals("Buzz", fizzBuzz.evaluate(5));
        assertEquals("Buzz", fizzBuzz.evaluate(10));
        assertEquals("Buzz", fizzBuzz.evaluate(20));
        assertEquals("Buzz", fizzBuzz.evaluate(25));
    }

    // --- ITERACION 4: multiplo de 15 ---
    @Test
    @Order(4)
    @DisplayName("[Iteracion 4 - RED->GREEN] Multiplo de 15 debe retornar 'FizzBuzz'")
    void multiploQuinceDebe RetornarFizzBuzz() {
        assertEquals("FizzBuzz", fizzBuzz.evaluate(15));
        assertEquals("FizzBuzz", fizzBuzz.evaluate(30));
        assertEquals("FizzBuzz", fizzBuzz.evaluate(45));
        assertEquals("FizzBuzz", fizzBuzz.evaluate(60));
    }

    // --- ITERACION 5: REFACTOR - prueba parametrizada ---
    @ParameterizedTest
    @Order(5)
    @CsvSource({
        "1,  1",
        "2,  2",
        "3,  Fizz",
        "5,  Buzz",
        "6,  Fizz",
        "9,  Fizz",
        "10, Buzz",
        "12, Fizz",
        "14, 14",
        "15, FizzBuzz",
        "30, FizzBuzz",
        "31, 31",
        "33, Fizz",
        "35, Buzz",
        "45, FizzBuzz",
        "100,Buzz"
    })
    @DisplayName("[Iteracion 5 - REFACTOR] Verificacion completa de FizzBuzz con prueba parametrizada")
    void verificacionCompletaFizzBuzz(int numero, String esperado) {
        assertEquals(esperado.trim(), fizzBuzz.evaluate(numero),
            "Para n=" + numero + " se esperaba: " + esperado.trim());
    }

    // --- CASOS BORDE ---
    @Test
    @Order(6)
    @DisplayName("[Borde] Debe manejar el numero 0")
    void debeManejarElCero() {
        // 0 es multiplo de todo, por convencion retornamos FizzBuzz
        assertEquals("FizzBuzz", fizzBuzz.evaluate(0));
    }

    @Test
    @Order(7)
    @DisplayName("[Borde] Debe manejar numeros negativos")
    void debeManejarNumerosNegativos() {
        assertEquals("Fizz", fizzBuzz.evaluate(-3));
        assertEquals("Buzz", fizzBuzz.evaluate(-5));
        assertEquals("FizzBuzz", fizzBuzz.evaluate(-15));
        assertEquals("-1", fizzBuzz.evaluate(-1));
    }

    // =========================================================
    // IMPLEMENTACION DE FIZZBU ZZ (normalmente en archivo separado)
    // Se incluye aqui para mostrar la evolucion del codigo en TDD
    // =========================================================

    /**
     * Implementacion final de FizzBuzz tras el ciclo Red-Green-Refactor.
     *
     * EVOLUCION:
     * v1 (tras Iter.1): return String.valueOf(n);
     * v2 (tras Iter.2): if (n%3==0) return "Fizz"; return String.valueOf(n);
     * v3 (tras Iter.3): if (n%3==0) return "Fizz"; if(n%5==0) return "Buzz"...
     * v4 (tras Iter.4): Agregar if(n%15==0) PRIMERO para evitar bug
     * v5 (REFACTOR):   Usar StringBuilder para claridad y extension futura
     */
    static class FizzBuzz {
        public String evaluate(int n) {
            // REFACTOR: usar StringBuilder permite agregar nuevas reglas facilmente
            StringBuilder result = new StringBuilder();

            if (n % 3 == 0) result.append("Fizz");
            if (n % 5 == 0) result.append("Buzz");

            // Si result esta vacio, no era multiplo de 3 ni 5
            return result.isEmpty() ? String.valueOf(n) : result.toString();
        }
    }
}
