package com.example.finanzas.model;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Movimiento implements Identifiable {
    private String id;
    private String usuarioId;
    private final ObjectProperty<LocalDate> fecha = new SimpleObjectProperty<>();
    private final StringProperty tipo = new SimpleStringProperty();       // INGRESO | GASTO
    private final DoubleProperty monto = new SimpleDoubleProperty();
    private final StringProperty concepto = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final StringProperty cuenta = new SimpleStringProperty();     // cuenta origen/destino
    private final StringProperty estado = new SimpleStringProperty();     // Aplicado | Pendiente | Conciliado
    private final StringProperty metodoPago = new SimpleStringProperty();
    private final StringProperty etiquetas = new SimpleStringProperty();  // "#vacaciones #becas"

    public Movimiento(String id, String usuarioId, LocalDate fecha, String tipo, double monto, String concepto,
                      String categoria, String cuenta, String estado, String metodoPago, String etiquetas) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha.set(fecha);
        this.tipo.set(tipo);
        this.monto.set(monto);
        this.concepto.set(concepto);
        this.categoria.set(categoria);
        this.cuenta.set(cuenta);
        this.estado.set(estado);
        this.metodoPago.set(metodoPago);
        this.etiquetas.set(etiquetas == null ? "" : etiquetas);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }

    public LocalDate getFecha() { return fecha.get(); }
    public void setFecha(LocalDate fecha) { this.fecha.set(fecha); }
    public ObjectProperty<LocalDate> fechaProperty() { return fecha; }

    public String getTipo() { return tipo.get(); }
    public void setTipo(String tipo) { this.tipo.set(tipo); }
    public StringProperty tipoProperty() { return tipo; }

    public double getMonto() { return monto.get(); }
    public void setMonto(double monto) { this.monto.set(monto); }
    public DoubleProperty montoProperty() { return monto; }

    public String getConcepto() { return concepto.get(); }
    public void setConcepto(String concepto) { this.concepto.set(concepto); }
    public StringProperty conceptoProperty() { return concepto; }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }
    public StringProperty categoriaProperty() { return categoria; }

    public String getCuenta() { return cuenta.get(); }
    public void setCuenta(String cuenta) { this.cuenta.set(cuenta); }
    public StringProperty cuentaProperty() { return cuenta; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    public String getMetodoPago() { return metodoPago.get(); }
    public void setMetodoPago(String metodoPago) { this.metodoPago.set(metodoPago); }
    public StringProperty metodoPagoProperty() { return metodoPago; }

    public String getEtiquetas() { return etiquetas.get(); }
    public void setEtiquetas(String etiquetas) { this.etiquetas.set(etiquetas); }
    public StringProperty etiquetasProperty() { return etiquetas; }

    public List<String> getEtiquetasLista() { return parseEtiquetas(getEtiquetas()); }

    /** "vacaciones, #becas  impuestos" -> [#vacaciones, #becas, #impuestos] */
    public static List<String> parseEtiquetas(String texto) {
        List<String> out = new ArrayList<>();
        if (texto == null) return out;
        for (String t : texto.trim().split("[,\\s]+")) {
            if (t.isBlank()) continue;
            String n = (t.startsWith("#") ? t : "#" + t).toLowerCase(Locale.ROOT);
            if (!out.contains(n)) out.add(n);
        }
        return out;
    }
}
