package com.example.finanzas.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;

public final class Tasks {
    private Tasks() {}

    public static <T> Task<T> of(Callable<T> work) {
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
    }
}
