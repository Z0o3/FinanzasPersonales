package com.example.finanzas.model;

/** Fila de la tabla de presupuestos: tope + gasto acumulado del mes actual. */
public class PresupuestoResumen {
    private final Presupuesto presupuesto;
    private final double gastado;

    public PresupuestoResumen(Presupuesto presupuesto, double gastado) {
        this.presupuesto = presupuesto;
        this.gastado = gastado;
    }

    public Presupuesto getPresupuesto() { return presupuesto; }
    public String getCategoria() { return presupuesto.getCategoria(); }
    public double getLimite() { return presupuesto.getLimiteMensual(); }
    public double getGastado() { return gastado; }
    public double getDisponible() { return getLimite() - gastado; }
    public double getPorcentaje() { return getLimite() <= 0 ? 0 : gastado / getLimite(); }
}
