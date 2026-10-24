package com.example.finanzas.report;

/** Patrón FACTORY: decide qué estrategia concreta crear según el formato elegido. */
public final class ReportFactory {
    private ReportFactory() {}

    public static ReportStrategy crear(FormatoReporte formato) {
        return switch (formato) {
            case PDF -> new PdfReportStrategy();
            case XLSX -> new XlsxReportStrategy();
        };
    }
}
