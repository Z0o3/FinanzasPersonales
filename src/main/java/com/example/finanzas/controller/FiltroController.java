package com.example.finanzas.controller;

import com.example.finanzas.service.FiltroMovimientos;
import com.example.finanzas.util.Catalogos;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.Locale;

/** Componente de filtros reutilizado en Movimientos y Reportes. */
public class FiltroController {
    @FXML private TextField txtTexto;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtEtiqueta;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    private Runnable onChange = () -> {};
    private boolean ajustando;

    @FXML
    private void initialize() {
        cmbTipo.getItems().addAll("Todos", "INGRESO", "GASTO");
        cmbTipo.setValue("Todos");
        cmbEstado.getItems().add("Todos");
        cmbEstado.getItems().addAll(Catalogos.ESTADOS);
        cmbEstado.setValue("Todos");
        cmbCategoria.getItems().add("Todas");
        cmbCategoria.setValue("Todas");

        cmbCategoria.valueProperty().addListener((o, a, b) -> fire());
        cmbTipo.valueProperty().addListener((o, a, b) -> fire());
        cmbEstado.valueProperty().addListener((o, a, b) -> fire());
        txtTexto.textProperty().addListener((o, a, b) -> fire());
        txtEtiqueta.textProperty().addListener((o, a, b) -> fire());
        dpDesde.valueProperty().addListener((o, a, b) -> fire());
        dpHasta.valueProperty().addListener((o, a, b) -> fire());
    }

    private void fire() {
        if (!ajustando) onChange.run();
    }

    public void setOnChange(Runnable r) { this.onChange = r; }

    public void setCategorias(Collection<String> nombres) {
        ajustando = true;
        String sel = cmbCategoria.getValue();
        cmbCategoria.getItems().setAll("Todas");
        cmbCategoria.getItems().addAll(nombres);
        cmbCategoria.setValue(sel != null && cmbCategoria.getItems().contains(sel) ? sel : "Todas");
        ajustando = false;
        fire();
    }

    @FXML
    private void onClear() {
        ajustando = true;
        cmbTipo.setValue("Todos");
        cmbEstado.setValue("Todos");
        cmbCategoria.setValue("Todas");
        txtTexto.clear();
        txtEtiqueta.clear();
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        ajustando = false;
        fire();
    }

    public FiltroMovimientos getFiltro() {
        FiltroMovimientos f = new FiltroMovimientos();
        f.categoria = "Todas".equals(cmbCategoria.getValue()) ? null : cmbCategoria.getValue();
        f.tipo = "Todos".equals(cmbTipo.getValue()) ? null : cmbTipo.getValue();
        f.estado = "Todos".equals(cmbEstado.getValue()) ? null : cmbEstado.getValue();
        String et = txtEtiqueta.getText() == null ? "" : txtEtiqueta.getText().trim().toLowerCase(Locale.ROOT);
        f.etiqueta = et.isEmpty() ? null : (et.startsWith("#") ? et : "#" + et);
        f.texto = txtTexto.getText();
        f.desde = dpDesde.getValue();
        f.hasta = dpHasta.getValue();
        return f;
    }
}
