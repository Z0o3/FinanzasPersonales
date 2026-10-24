package com.example.finanzas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Dialogs {
    private Dialogs() {}

    public static void info(String titulo, String msg) { show(Alert.AlertType.INFORMATION, titulo, msg); }
    public static void warn(String titulo, String msg) { show(Alert.AlertType.WARNING, titulo, msg); }
    public static void error(String msg) { show(Alert.AlertType.ERROR, "Ocurrió un error", msg); }

    private static void show(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type, msg);
        a.setTitle(titulo);
        a.setHeaderText(titulo);
        a.show();
    }

    public static boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO)
                .showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }
}
