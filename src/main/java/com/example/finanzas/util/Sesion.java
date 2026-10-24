package com.example.finanzas.util;

import com.example.finanzas.model.Usuario;

public final class Sesion {
    private static Usuario actual;

    private Sesion() {}

    public static void iniciar(Usuario u) { actual = u; }
    public static void cerrar() { actual = null; }
    public static Usuario getUsuario() { return actual; }
    public static String uid() { return actual.getId(); }
}
