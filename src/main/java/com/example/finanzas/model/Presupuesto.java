package com.example.finanzas.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Presupuesto implements Identifiable {
    private String id;
    private String usuarioId;
    private final StringProperty categoria = new SimpleStringProperty();
    private final DoubleProperty limiteMensual = new SimpleDoubleProperty();
    private final DoubleProperty umbralAviso = new SimpleDoubleProperty(0.8); // avisa al 80 %

    public Presupuesto(String id, String usuarioId, String categoria, double limiteMensual, double umbralAviso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoria.set(categoria);
        this.limiteMensual.set(limiteMensual);
        this.umbralAviso.set(umbralAviso);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }
    public StringProperty categoriaProperty() { return categoria; }

    public double getLimiteMensual() { return limiteMensual.get(); }
    public void setLimiteMensual(double v) { this.limiteMensual.set(v); }
    public DoubleProperty limiteMensualProperty() { return limiteMensual; }

    public double getUmbralAviso() { return umbralAviso.get(); }
    public void setUmbralAviso(double v) { this.umbralAviso.set(v); }
    public DoubleProperty umbralAvisoProperty() { return umbralAviso; }
}
