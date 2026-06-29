package com.tdd.taskmanager.repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Proveedor del EntityManagerFactory para gestion del ciclo de vida de JPA.
 * Implementa el patron Singleton para compartir una unica instancia.
 */
public class JPAUtil {

    private static EntityManagerFactory emf;
    private static String currentPU = "taskmanager-pu";

    private JPAUtil() {}

    /**
     * Inicializa con la unidad de persistencia indicada.
     * Llamar con "taskmanager-test-pu" en pruebas.
     */
    public static synchronized void initWithPU(String persistenceUnit) {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        currentPU = persistenceUnit;
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(currentPU);
        }
        return emf;
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
