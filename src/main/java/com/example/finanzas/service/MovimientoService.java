package com.example.finanzas.service;

import com.example.finanzas.dao.MovimientoDAO;
import com.example.finanzas.model.Movimiento;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

import java.util.List;

public class MovimientoService {
    private final MovimientoDAO dao;

    public MovimientoService(MovimientoDAO dao) {
        this.dao = dao;
    }

    public Task<List<Movimiento>> loadTask(String usuarioId) {
        return Tasks.of(() -> dao.getAll(usuarioId));
    }

    public Task<Void> saveTask(Movimiento m) {
        return Tasks.of(() -> {
            if (m.getId() == null) dao.insert(m); else dao.update(m);
            return null;
        });
    }

    public Task<Void> deleteTask(String usuarioId, String id) {
        return Tasks.of(() -> {
            dao.delete(usuarioId, id);
            return null;
        });
    }
}
