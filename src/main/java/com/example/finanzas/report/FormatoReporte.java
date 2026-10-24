package com.example.finanzas.report;

public enum FormatoReporte {
    PDF("PDF (.pdf)"), XLSX("Excel (.xlsx)");

    private final String etiqueta;

    FormatoReporte(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
