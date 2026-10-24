package com.example.finanzas.controller;

import com.example.finanzas.util.Sesion;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private Label lblUser;
    @FXML private StackPane loadingOverlay;

    // Controladores de las pestañas incluidas con <fx:include fx:id="xxx"> (se inyectan como xxxController)
    @FXML private MovimientosController movimientosController;
    @FXML private PresupuestosController presupuestosController;
    @FXML private CategoriasController categoriasController;
    @FXML private GraficosController graficosController;
    @FXML private ReportesController reportesController;

    private Runnable onLogout;

    public void initialize(FinanzasViewModel vm, Runnable onLogout) {
        this.onLogout = onLogout;
        lblUser.setText("Usuario: " + Sesion.getUsuario().getNombreCompleto());
        loadingOverlay.visibleProperty().bind(vm.processingProperty());

        movimientosController.initialize(vm);
        presupuestosController.initialize(vm);
        categoriasController.initialize(vm);
        graficosController.initialize(vm);
        reportesController.initialize(vm);
        vm.loadAll(null);
    }

    @FXML
    private void onLogout() {
        onLogout.run();
    }
}
