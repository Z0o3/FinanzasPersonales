package com.example.finanzas.service;

import com.example.finanzas.dao.CategoriaDAO;
import com.example.finanzas.dao.UsuarioDAO;
import com.example.finanzas.model.Usuario;
import com.example.finanzas.util.Catalogos;
import com.example.finanzas.util.PasswordUtil;
import com.example.finanzas.util.Tasks;
import javafx.concurrent.Task;

public class AuthService {
    private final UsuarioDAO usuarioDao;
    private final CategoriaDAO categoriaDao;

    public AuthService(UsuarioDAO usuarioDao, CategoriaDAO categoriaDao) {
        this.usuarioDao = usuarioDao;
        this.categoriaDao = categoriaDao;
    }

    public Task<Usuario> loginTask(String nombre, String password) {
        return Tasks.of(() -> {
            Usuario u = usuarioDao.findByNombre(nombre == null ? "" : nombre.trim());
            if (u == null || !PasswordUtil.hash(password, u.getSalt()).equals(u.getPasswordHash()))
                throw new IllegalArgumentException("Usuario o contraseña incorrectos");
            return u;
        });
    }

    public Task<Usuario> registrarTask(String nombre, String password) {
        return Tasks.of(() -> registrar(nombre, password, nombre));
    }

    /** Operación síncrona; siempre se invoca desde una Task (hilo de fondo). */
    public Usuario registrar(String nombre, String password, String nombreCompleto) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El usuario es obligatorio");
        if (!nombre.trim().matches("[^.$#\\[\\]/\\s]+"))
            throw new IllegalArgumentException("El usuario no puede tener espacios ni los caracteres . $ # [ ] /");
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        if (usuarioDao.findByNombre(nombre.trim()) != null) throw new IllegalArgumentException("Ese usuario ya existe");
        String sal = PasswordUtil.nuevaSal();
        Usuario u = new Usuario(null, nombre.trim(), nombreCompleto, PasswordUtil.hash(password, sal), sal);
        usuarioDao.insert(u);
        categoriaDao.insertAll(Catalogos.categoriasBase(u.getId()));
        return u;
    }
}
