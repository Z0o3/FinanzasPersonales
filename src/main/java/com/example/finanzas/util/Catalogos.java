package com.example.finanzas.util;

import com.example.finanzas.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public final class Catalogos {
    private Catalogos() {}

    public static final List<String> TIPOS = List.of("INGRESO", "GASTO");
    public static final List<String> ESTADOS = List.of("Aplicado", "Pendiente", "Conciliado");
    public static final List<String> METODOS = List.of("Efectivo", "Tarjeta de crédito", "Tarjeta de débito",
            "Transferencia", "SPEI", "Depósito", "Cheque");
    public static final List<String> CUENTAS = List.of("Efectivo", "Cuenta Nómina", "Tarjeta de crédito",
            "Ahorros", "Cuenta Digital");

    private static final String[] GASTOS = {"Comida", "Transporte", "Servicios", "Entretenimiento",
            "Salud", "Educación", "Hogar", "Ropa"};
    private static final String[] INGRESOS = {"Nómina", "Freelance", "Becas", "Otros ingresos"};

    public static List<Categoria> categoriasBase(String uid) {
        List<Categoria> l = new ArrayList<>();
        for (String g : GASTOS) l.add(new Categoria(null, uid, g, "GASTO"));
        for (String i : INGRESOS) l.add(new Categoria(null, uid, i, "INGRESO"));
        return l;
    }
}
