package com.example.finanzas.controller;

import com.example.finanzas.model.Movimiento;
import com.example.finanzas.report.FormatoReporte;
import com.example.finanzas.util.Dialogs;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportesController {
    @FXML private FiltroController filtroController;
    @FXML private Label lblSummary;
    @FXML private ComboBox<FormatoReporte> cmbFormato;
    @FXML private Button btnExport;
    @FXML private Label lblStatus;

    private FinanzasViewModel viewModel;

    public void initialize(FinanzasViewModel vm) {
        this.viewModel = vm;
        cmbFormato.setItems(FXCollections.observableArrayList(FormatoReporte.values()));
        cmbFormato.setValue(FormatoReporte.PDF);
        btnExport.disableProperty().bind(vm.processingProperty());

        filtroController.setOnChange(this::actualizarResumen);
        vm.getCategorias().addListener((InvalidationListener) o -> filtroController.setCategorias(vm.nombresCategorias(null)));
        vm.getMovimientos().addListener((InvalidationListener) o -> actualizarResumen());
    }

    private void actualizarResumen() {
        lblSummary.setText(filtroController.getFiltro().aplicar(viewModel.getMovimientos()).size()
                + " movimiento(s) coinciden con los filtros y se exportarán.");
    }

    @FXML
    private void onExport() {
        List<Movimiento> datos = new ArrayList<>(filtroController.getFiltro().aplicar(viewModel.getMovimientos()));
        if (datos.isEmpty()) {
            Dialogs.warn("Sin datos", "No hay movimientos con esos filtros.");
            return;
        }
        FormatoReporte formato = cmbFormato.getValue();
        String ext = formato == FormatoReporte.PDF ? "pdf" : "xlsx";
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("reporte_movimientos." + ext);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato.toString(), "*." + ext));
        File destino = fc.showSaveDialog(btnExport.getScene().getWindow());
        if (destino == null) return;

        lblStatus.setText("Generando reporte en segundo plano...");
        viewModel.exportar(formato, datos, destino, f -> {
            lblStatus.setText("Reporte guardado en: " + f.getAbsolutePath());
            Dialogs.info("Reporte generado", f.getAbsolutePath());
        });
    }
}
