package com.example.finanzas.dao;

import com.example.finanzas.config.FirebaseConfig;
import com.example.finanzas.model.Identifiable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación base de los DAO sobre Firebase Realtime Database (REST).
 * Estructura en la base:  /{coleccion}/{usuarioId}/{id} -> objeto JSON.
 */
public abstract class RealtimeDAO<T extends Identifiable> implements CrudDAO<T> {
    protected final FirebaseConfig fb = FirebaseConfig.getInstance();

    protected abstract String collection();
    protected abstract String owner(T t);
    protected abstract T fromJson(String usuarioId, String id, JsonObject o);
    protected abstract JsonObject toJson(T t);

    @Override
    public List<T> getAll(String usuarioId) {
        List<T> list = new ArrayList<>();
        String r = fb.get(collection(), usuarioId);
        if (r == null || r.isBlank() || "null".equals(r.trim())) return list;
        for (Map.Entry<String, JsonElement> e : JsonParser.parseString(r).getAsJsonObject().entrySet()) {
            if (e.getValue().isJsonObject()) list.add(fromJson(usuarioId, e.getKey(), e.getValue().getAsJsonObject()));
        }
        return list;
    }

    @Override
    public void insert(T t) {
        String id = UUID.randomUUID().toString();
        fb.put(toJson(t).toString(), collection(), owner(t), id);
        t.setId(id);
    }

    /** Un solo PATCH con todos los registros nuevos (rápido para los datos de demostración). */
    @Override
    public void insertAll(List<T> lista) {
        if (lista.isEmpty()) return;
        JsonObject lote = new JsonObject();
        for (T t : lista) {
            String id = UUID.randomUUID().toString();
            lote.add(id, toJson(t));
            t.setId(id);
        }
        fb.patch(lote.toString(), collection(), owner(lista.get(0)));
    }

    @Override
    public void update(T t) {
        fb.put(toJson(t).toString(), collection(), owner(t), t.getId());
    }

    @Override
    public void delete(String usuarioId, String id) {
        fb.delete(collection(), usuarioId, id);
    }

    protected static String str(JsonObject o, String k) {
        JsonElement e = o.get(k);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    protected static double num(JsonObject o, String k, double def) {
        JsonElement e = o.get(k);
        return e == null || e.isJsonNull() ? def : e.getAsDouble();
    }
}
