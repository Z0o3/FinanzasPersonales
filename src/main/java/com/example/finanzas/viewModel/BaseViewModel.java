package com.example.finanzas.viewModel;

import com.example.finanzas.config.TaskExecutor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import java.util.function.Consumer;

/**
 * Ejecuta las Task en el pool de hilos y mantiene la propiedad "processing" (true mientras haya
 * al menos una tarea en curso). La vista la enlaza al LoadingOverlay.
 */
public abstract class BaseViewModel {
    private static Consumer<Throwable> errorHandler = Throwable::printStackTrace;

    private final BooleanProperty processing = new SimpleBooleanProperty();
    private int pending;

    public static void setErrorHandler(Consumer<Throwable> handler) {
        errorHandler = handler;
    }

    public BooleanProperty processingProperty() { return processing; }

    protected <T> void execute(Task<T> task, Consumer<T> onSuccess) {
        execute(task, onSuccess, errorHandler);
    }

    protected <T> void execute(Task<T> task, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        pending++;
        processing.set(true);
        task.setOnSucceeded(e -> {
            finish();
            if (onSuccess != null) onSuccess.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            finish();
            onError.accept(task.getException());
        });
        TaskExecutor.getInstance().execute(task);
    }

    private void finish() {
        pending--;
        processing.set(pending > 0);
    }
}
