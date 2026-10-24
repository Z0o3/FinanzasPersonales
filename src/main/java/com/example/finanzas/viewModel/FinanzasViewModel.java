package com.example.finanzas.viewModel;

import com.example.finanzas.model.Categoria;
import com.example.finanzas.model.Movimiento;
import com.example.finanzas.model.Presupuesto;
import com.example.finanzas.report.FormatoReporte;
import com.example.finanzas.service.*;
import com.example.finanzas.util.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** ViewModel compartido por todas las pestañas: las listas observables notifican a tablas y gráficos. */
public class FinanzasViewModel extends BaseViewModel {
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final PresupuestoService presupuestoService;
    private final ReportService reportService;

    private final ObservableList<Movimiento> movimientos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> categorias = FXCollections.observableArrayList();
    private final ObservableList<Presupuesto> presupuestos = FXCollections.observableArrayList();
    private int cargasPendientes;

    public FinanzasViewModel(MovimientoService movimientoService, CategoriaService categoriaService,
                             PresupuestoService presupuestoService, ReportService reportService) {
        this.movimientoService = movimientoService;
        this.categoriaService = categoriaService;
        this.presupuestoService = presupuestoService;
        this.reportService = reportService;
    }

    public ObservableList<Movimiento> getMovimientos() { return movimientos; }
    public ObservableList<Categoria> getCategorias() { return categorias; }
    public ObservableList<Presupuesto> getPresupuestos() { return presupuestos; }

    public List<String> nombresCategorias(String tipo) {
        List<String> out = new ArrayList<>();
        for (Categoria c : categorias) if (tipo == null || tipo.equals(c.getTipo())) out.add(c.getNombre());
        out.sort(String::compareToIgnoreCase);
        return out;
    }

    /** Carga las tres colecciones en paralelo (tres tareas concurrentes). */
    public void loadAll(Runnable onDone) {
        String uid = Sesion.uid();
        cargasPendientes = 3;
        Runnable fin = () -> {
            if (--cargasPendientes == 0 && onDone != null) onDone.run();
        };
        execute(movimientoService.loadTask(uid), l -> { movimientos.setAll(l); fin.run(); });
        execute(categoriaService.loadTask(uid), l -> { categorias.setAll(l); fin.run(); });
        execute(presupuestoService.loadTask(uid), l -> { presupuestos.setAll(l); fin.run(); });
    }

    // ---- Movimientos
    public void saveMovimiento(Movimiento m, Consumer<String> onAlerta) {
        execute(movimientoService.saveTask(m), v -> loadAll(() ->
                PresupuestoService.alerta(movimientos, presupuestos, m).ifPresent(onAlerta)));
    }

    public void deleteMovimiento(Movimiento m) {
        execute(movimientoService.deleteTask(Sesion.uid(), m.getId()), v -> loadAll(null));
    }

    // ---- Categorías
    public void saveCategoria(Categoria c, String nombreAnterior) {
        execute(categoriaService.saveTask(c, nombreAnterior), v -> loadAll(null));
    }

    public void deleteCategoria(Categoria c) {
        execute(categoriaService.deleteTask(Sesion.uid(), c.getId()), v -> loadAll(null));
    }

    // ---- Presupuestos
    public void savePresupuesto(Presupuesto p) {
        execute(presupuestoService.saveTask(p), v -> loadAll(null));
    }

    public void deletePresupuesto(Presupuesto p) {
        execute(presupuestoService.deleteTask(Sesion.uid(), p.getId()), v -> loadAll(null));
    }

    // ---- Reportes
    public void exportar(FormatoReporte formato, List<Movimiento> datos, File destino, Consumer<File> onDone) {
        execute(reportService.exportTask(formato, datos, destino, "Reporte de movimientos financieros"), onDone);
    }
}
