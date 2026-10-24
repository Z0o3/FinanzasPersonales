package com.example.finanzas.report;

import com.example.finanzas.model.Movimiento;

import java.io.File;
import java.util.List;

/** Patrón STRATEGY: cada formato implementa su propio algoritmo de generación. */
public interface ReportStrategy {
    String extension();
    void generar(List<Movimiento> datos, File destino, String titulo) throws Exception;
}
