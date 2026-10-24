package com.example.finanzas.dao;

import com.example.finanzas.config.FirebaseConfig;
import com.example.finanzas.model.Usuario;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Locale;

/** Los usuarios se guardan en /usuarios/{nombre en minúsculas}, así el login no necesita consultas con índice. */
public class UsuarioDAOImpl implements UsuarioDAO {
    private final FirebaseConfig fb = FirebaseConfig.getInstance();

    private static String clave(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "_");   // Firebase no admite . $ # [ ] / en las claves
    }

    @Override
    public Usuario findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) return null;
        String id = clave(nombre);
        String r = fb.get("usuarios", id);
        if (r == null || "null".equals(r.trim())) return null;
        JsonObject o = JsonParser.parseString(r).getAsJsonObject();
        return new Usuario(id, o.get("nombre").getAsString(), o.get("nombreCompleto").getAsString(),
                o.get("passwordHash").getAsString(), o.get("salt").getAsString());
    }

    @Override
    public void insert(Usuario u) {
        String id = clave(u.getNombre());
        JsonObject o = new JsonObject();
        o.addProperty("nombre", u.getNombre());
        o.addProperty("nombreCompleto", u.getNombreCompleto());
        o.addProperty("passwordHash", u.getPasswordHash());
        o.addProperty("salt", u.getSalt());
        fb.put(o.toString(), "usuarios", id);
        u.setId(id);
    }
}
