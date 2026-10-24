package com.example.finanzas.dao;

import com.example.finanzas.model.Movimiento;
import com.google.gson.JsonObject;

import java.time.LocalDate;

public class MovimientoDAOImpl extends RealtimeDAO<Movimiento> implements MovimientoDAO {
    @Override
    protected String collection() { return "movimientos"; }

    @Override
    protected String owner(Movimiento m) { return m.getUsuarioId(); }

    @Override
    protected Movimiento fromJson(String usuarioId, String id, JsonObject o) {
        return new Movimiento(id, usuarioId, LocalDate.parse(str(o, "fecha")), str(o, "tipo"), num(o, "monto", 0),
                str(o, "concepto"), str(o, "categoria"), str(o, "cuenta"), str(o, "estado"),
                str(o, "metodoPago"), str(o, "etiquetas"));
    }

    @Override
    protected JsonObject toJson(Movimiento m) {
        JsonObject o = new JsonObject();
        o.addProperty("fecha", m.getFecha().toString());
        o.addProperty("tipo", m.getTipo());
        o.addProperty("monto", m.getMonto());
        o.addProperty("concepto", m.getConcepto());
        o.addProperty("categoria", m.getCategoria());
        o.addProperty("cuenta", m.getCuenta());
        o.addProperty("estado", m.getEstado());
        o.addProperty("metodoPago", m.getMetodoPago());
        o.addProperty("etiquetas", m.getEtiquetas());
        return o;
    }
}
