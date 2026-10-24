package com.example.finanzas.dao;

import com.example.finanzas.model.Presupuesto;
import com.google.gson.JsonObject;

public class PresupuestoDAOImpl extends RealtimeDAO<Presupuesto> implements PresupuestoDAO {
    @Override
    protected String collection() { return "presupuestos"; }

    @Override
    protected String owner(Presupuesto p) { return p.getUsuarioId(); }

    @Override
    protected Presupuesto fromJson(String usuarioId, String id, JsonObject o) {
        return new Presupuesto(id, usuarioId, str(o, "categoria"), num(o, "limiteMensual", 0), num(o, "umbralAviso", 0.8));
    }

    @Override
    protected JsonObject toJson(Presupuesto p) {
        JsonObject o = new JsonObject();
        o.addProperty("categoria", p.getCategoria());
        o.addProperty("limiteMensual", p.getLimiteMensual());
        o.addProperty("umbralAviso", p.getUmbralAviso());
        return o;
    }
}
