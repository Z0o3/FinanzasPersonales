package com.example.finanzas.service;

import com.example.finanzas.dao.CategoriaDAO;
import com.example.finanzas.dao.MovimientoDAO;
import com.example.finanzas.dao.PresupuestoDAO;
import com.example.finanzas.model.Categoria;
import com.example.finanzas.model.Movimiento;
import com.example.finanzas.model.Presupuesto;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

import java.util.List;

public class CategoriaService {
    private final CategoriaDAO dao;
    private final MovimientoDAO movimientoDao;
    private final PresupuestoDAO presupuestoDao;

    public CategoriaService(CategoriaDAO dao, MovimientoDAO movimientoDao, PresupuestoDAO presupuestoDao) {
        this.dao = dao;
        this.movimientoDao = movimientoDao;
        this.presupuestoDao = presupuestoDao;
    }

    public Task<List<Categoria>> loadTask(String usuarioId) {
        return Tasks.of(() -> dao.getAll(usuarioId));
    }

    /** Si se renombra, el cambio se propaga a movimientos y presupuestos. */
    public Task<Void> saveTask(Categoria c, String nombreAnterior) {
        return Tasks.of(() -> {
            if (c.getId() == null) {
                dao.insert(c);
                return null;
            }
            dao.update(c);
            if (nombreAnterior != null && !nombreAnterior.equals(c.getNombre())) {
                for (Movimiento m : movimientoDao.getAll(c.getUsuarioId())) {
                    if (nombreAnterior.equals(m.getCategoria())) {
                        m.setCategoria(c.getNombre());
                        movimientoDao.update(m);
                    }
                }
                for (Presupuesto p : presupuestoDao.getAll(c.getUsuarioId())) {
                    if (nombreAnterior.equals(p.getCategoria())) {
                        p.setCategoria(c.getNombre());
                        presupuestoDao.update(p);
                    }
                }
            }
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
