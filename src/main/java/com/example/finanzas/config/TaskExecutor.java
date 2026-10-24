package com.example.finanzas.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Singleton con el pool de hilos donde se ejecutan todas las tareas asíncronas. */
public class TaskExecutor {
    private static TaskExecutor instance;
    private final ExecutorService pool = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "finanzas-worker");
        t.setDaemon(true);
        return t;
    });

    private TaskExecutor() {}

    public static synchronized TaskExecutor getInstance() {
        if (instance == null) {
            instance = new TaskExecutor();
        }
        return instance;
    }

    public void execute(Runnable task) {
        pool.execute(task);
    }

    public void shutdown() {
        pool.shutdownNow();
    }
}
