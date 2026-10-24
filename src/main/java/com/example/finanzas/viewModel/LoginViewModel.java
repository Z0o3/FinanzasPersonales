package com.example.finanzas.viewModel;

import com.example.finanzas.service.AuthService;
import com.example.finanzas.service.DemoDataSeeder;
import com.example.finanzas.util.Sesion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel extends BaseViewModel {
    private final AuthService auth;
    private final DemoDataSeeder seeder;
    private final StringProperty status = new SimpleStringProperty("Conectando con Firebase y preparando datos de demostración...");
    private final BooleanProperty ready = new SimpleBooleanProperty(false);

    public LoginViewModel(AuthService auth, DemoDataSeeder seeder) {
        this.auth = auth;
        this.seeder = seeder;
    }

    public StringProperty statusProperty() { return status; }
    public BooleanProperty readyProperty() { return ready; }

    /** Siembra los datos de demostración en segundo plano antes de habilitar el login. */
    public void prepare() {
        execute(seeder.seedTask(), v -> {
            ready.set(true);
            status.set("Listo. Usuario de prueba: demo / demo123");
        }, ex -> status.set("Error de conexión: " + ex.getMessage()));
    }

    public void login(String user, String pass, Runnable onSuccess) {
        status.set("Validando...");
        execute(auth.loginTask(user, pass), u -> {
            Sesion.iniciar(u);
            onSuccess.run();
        }, ex -> status.set(ex.getMessage()));
    }

    public void register(String user, String pass, Runnable onSuccess) {
        status.set("Registrando...");
        execute(auth.registrarTask(user, pass), u -> {
            Sesion.iniciar(u);
            onSuccess.run();
        }, ex -> status.set(ex.getMessage()));
    }
}
