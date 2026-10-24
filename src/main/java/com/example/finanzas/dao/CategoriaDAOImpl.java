package com.example.finanzas.dao;

import com.example.finanzas.model.Categoria;
import com.google.gson.JsonObject;

public class CategoriaDAOImpl extends RealtimeDAO<Categoria> implements CategoriaDAO {
    @Override
    protected String collection() { return "categorias"; }

    @Override
    protected String owner(Categoria c) { return c.getUsuarioId(); }

    @Override
    protected Categoria fromJson(String usuarioId, String id, JsonObject o) {
        return new Categoria(id, usuarioId, str(o, "nombre"), str(o, "tipo"));
    }

    @Override
    protected JsonObject toJson(Categoria c) {
        JsonObject o = new JsonObject();
        o.addProperty("nombre", c.getNombre());
        o.addProperty("tipo", c.getTipo());
        return o;
    }
}
