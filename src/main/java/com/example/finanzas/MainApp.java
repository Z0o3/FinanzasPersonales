package com.example.finanzas;

import com.example.finanzas.config.FirebaseConfig;
import com.example.finanzas.config.TaskExecutor;
import com.example.finanzas.controller.LoginController;
import com.example.finanzas.controller.MainController;
import com.example.finanzas.dao.*;
import com.example.finanzas.service.*;
import com.example.finanzas.util.Dialogs;
import com.example.finanzas.util.Sesion;
import com.example.finanzas.viewModel.BaseViewModel;
import com.example.finanzas.viewModel.FinanzasViewModel;
import com.example.finanzas.viewModel.LoginViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private Stage stage;
    private UsuarioDAO usuarioDao;
    private CategoriaDAO categoriaDao;
    private PresupuestoDAO presupuestoDao;
    private MovimientoDAO movimientoDao;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        BaseViewModel.setErrorHandler(t -> {
            t.printStackTrace();
            Dialogs.error(String.valueOf(t.getMessage()));
        });
        try {
            FirebaseConfig.getInstance().verificar();   // valida la URL y la conexión (Singleton)
            usuarioDao = new UsuarioDAOImpl();
            categoriaDao = new CategoriaDAOImpl();
            presupuestoDao = new PresupuestoDAOImpl();
            movimientoDao = new MovimientoDAOImpl();
        } catch (IllegalStateException e) {
            Dialogs.error(e.getMessage());
            Platform.exit();
            return;
        }

        stage.setTitle("Finanzas Personales");
        showLogin();
        stage.show();
    }

    private void showLogin() {
        try {
            Sesion.cerrar();
            var auth = new AuthService(usuarioDao, categoriaDao);
            var seeder = new DemoDataSeeder(usuarioDao, auth, presupuestoDao, movimientoDao);
            var viewModel = new LoginViewModel(auth, seeder);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 420, 420);
            LoginController controller = loader.getController();
            controller.initialize(viewModel, this::showMain);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showMain() {
        try {
            var viewModel = new FinanzasViewModel(
                    new MovimientoService(movimientoDao),
                    new CategoriaService(categoriaDao, movimientoDao, presupuestoDao),
                    new PresupuestoService(presupuestoDao),
                    new ReportService());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene scene = new Scene(loader.load(), 1250, 740);
            MainController controller = loader.getController();
            controller.initialize(viewModel, this::showLogin);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        TaskExecutor.getInstance().shutdown();
    }
}
