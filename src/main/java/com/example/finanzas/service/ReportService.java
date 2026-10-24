package com.example.finanzas.service;

import com.example.finanzas.model.Movimiento;
import com.example.finanzas.report.FormatoReporte;
import com.example.finanzas.report.ReportFactory;
import com.example.finanzas.report.ReportStrategy;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

/** Contexto del patrón Strategy: pide la estrategia a la Factory y delega la generación. */
public class ReportService {
    public Task<File> exportTask(FormatoReporte formato, List<Movimiento> datos, File destino, String titulo) {
        return Tasks.of(() -> {
            ReportStrategy estrategia = ReportFactory.crear(formato);
            File f = destino.getName().toLowerCase().endsWith(estrategia.extension())
                    ? destino : new File(destino.getParentFile(), destino.getName() + estrategia.extension());
            estrategia.generar(datos, f, titulo);
            return f;
        });
    }
}
