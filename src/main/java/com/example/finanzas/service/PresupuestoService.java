package com.example.finanzas.service;

import com.example.finanzas.dao.PresupuestoDAO;
import com.example.finanzas.model.Movimiento;
import com.example.finanzas.model.Presupuesto;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class PresupuestoService {
    private final PresupuestoDAO dao;

    public PresupuestoService(PresupuestoDAO dao) {
        this.dao = dao;
    }

    public Task<List<Presupuesto>> loadTask(String usuarioId) {
        return Tasks.of(() -> dao.getAll(usuarioId));
    }

    public Task<Void> saveTask(Presupuesto p) {
        return Tasks.of(() -> {
            if (p.getId() == null) dao.insert(p); else dao.update(p);
            return null;
        });
    }

    public Task<Void> deleteTask(String usuarioId, String id) {
        return Tasks.of(() -> {
            dao.delete(usuarioId, id);
            return null;
        });
    }

    /** Si el gasto deja la categoría cerca o por encima de su tope mensual, devuelve el aviso. */
    public static Optional<String> alerta(List<Movimiento> todos, List<Presupuesto> presupuestos, Movimiento m) {
        if (!"GASTO".equals(m.getTipo())) return Optional.empty();
        YearMonth ym = YearMonth.from(m.getFecha());
        for (Presupuesto p : presupuestos) {
            if (!p.getCategoria().equals(m.getCategoria()) || p.getLimiteMensual() <= 0) continue;
            double gasto = EstadisticaService.gastoMes(todos, p.getCategoria(), ym);
            double ratio = gasto / p.getLimiteMensual();
            if (ratio >= 1)
                return Optional.of(String.format("¡Has REBASADO el presupuesto de %s! Gastado $%,.2f de $%,.2f (%.0f%%).",
                        p.getCategoria(), gasto, p.getLimiteMensual(), ratio * 100));
            if (ratio >= p.getUmbralAviso())
                return Optional.of(String.format("Estás cerca del límite en %s: $%,.2f de $%,.2f (%.0f%%).",
                        p.getCategoria(), gasto, p.getLimiteMensual(), ratio * 100));
        }
        return Optional.empty();
    }
}
