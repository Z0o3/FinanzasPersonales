package com.example.finanzas.dao;

import com.example.finanzas.model.Usuario;

public interface UsuarioDAO {
    Usuario findByNombre(String nombre);
    void insert(Usuario u);
}
