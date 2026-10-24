package com.example.finanzas.util;

public final class Formato {
    private Formato() {}

    public static String dinero(double v) {
        return String.format("$%,.2f", v);
    }
}
