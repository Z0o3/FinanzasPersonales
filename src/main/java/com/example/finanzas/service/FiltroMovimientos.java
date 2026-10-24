package com.example.finanzas.service;

import com.example.finanzas.model.Movimiento;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Criterios de búsqueda compartidos por la tabla de movimientos y por los reportes. */
public class FiltroMovimientos implements Predicate<Movimiento> {
    public String categoria, tipo, estado, etiqueta, texto;
    public LocalDate desde, hasta;

    @Override
    public boolean test(Movimiento m) {
        if (categoria != null && !categoria.equals(m.getCategoria())) return false;
        if (tipo != null && !tipo.equals(m.getTipo())) return false;
        if (estado != null && !estado.equals(m.getEstado())) return false;
        if (etiqueta != null && m.getEtiquetasLista().stream().noneMatch(e -> e.equalsIgnoreCase(etiqueta))) return false;
        if (desde != null && m.getFecha().isBefore(desde)) return false;
        if (hasta != null && m.getFecha().isAfter(hasta)) return false;
        if (texto != null && !texto.isBlank()) {
            String q = texto.toLowerCase(Locale.ROOT).trim();
            String pajar = String.join(" ", m.getConcepto(), m.getCategoria(), m.getCuenta(), m.getMetodoPago(),
                    m.getEstado(), m.getEtiquetas(), String.valueOf(m.getMonto())).toLowerCase(Locale.ROOT);
            if (!pajar.contains(q)) return false;
        }
        return true;
    }

    public List<Movimiento> aplicar(List<Movimiento> lista) {
        return lista.stream().filter(this).collect(Collectors.toList());
    }
}
