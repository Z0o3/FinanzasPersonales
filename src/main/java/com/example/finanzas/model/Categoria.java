package com.example.finanzas.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categoria implements Identifiable {
    private String id;
    private String usuarioId;
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty tipo = new SimpleStringProperty(); // GASTO | INGRESO

    public Categoria(String id, String usuarioId, String nombre, String tipo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre.set(nombre);
        this.tipo.set(tipo);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }
}
