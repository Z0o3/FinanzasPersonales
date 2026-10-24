package com.example.finanzas.service;

import com.example.finanzas.model.Movimiento;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EstadisticaService {
    private EstadisticaService() {}

    /** Gasto total por categoría en un mes, de mayor a menor (detecta "fugas de dinero"). */
    public static Map<String, Double> gastosPorCategoria(List<Movimiento> l, YearMonth ym) {
        return l.stream()
                .filter(m -> "GASTO".equals(m.getTipo()) && YearMonth.from(m.getFecha()).equals(ym))
                .collect(Collectors.groupingBy(Movimiento::getCategoria, Collectors.summingDouble(Movimiento::getMonto)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    /** [0] = ingresos por mes, [1] = gastos por mes (12 posiciones) del año indicado. */
    public static double[][] ingresosVsGastos(List<Movimiento> l, int anio) {
        double[][] r = new double[2][12];
        for (Movimiento m : l) {
            if (m.getFecha().getYear() != anio) continue;
            r["INGRESO".equals(m.getTipo()) ? 0 : 1][m.getFecha().getMonthValue() - 1] += m.getMonto();
        }
        return r;
    }

    public static double gastoMes(List<Movimiento> l, String categoria, YearMonth ym) {
        return l.stream()
                .filter(m -> "GASTO".equals(m.getTipo()) && categoria.equals(m.getCategoria())
                        && YearMonth.from(m.getFecha()).equals(ym))
                .mapToDouble(Movimiento::getMonto).sum();
    }
}
