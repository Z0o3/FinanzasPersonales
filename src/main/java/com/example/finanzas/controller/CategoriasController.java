package com.example.finanzas.controller;

import com.example.finanzas.model.Categoria;
import com.example.finanzas.util.Catalogos;
import com.example.finanzas.util.Dialogs;
import com.example.finanzas.util.Sesion;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CategoriasController {
    @FXML private TableView<Categoria> table;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, String> colTipo;
    @FXML private Label lblForm;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;

    private FinanzasViewModel viewModel;
    private Categoria editando;

    public void initialize(FinanzasViewModel vm) {
        this.viewModel = vm;
        table.setItems(vm.getCategorias());
        colNombre.setCellValueFactory(d -> d.getValue().nombreProperty());
        colTipo.setCellValueFactory(d -> d.getValue().tipoProperty());
        cmbTipo.getItems().addAll("GASTO", "INGRESO");
        cmbTipo.setValue("GASTO");
        table.getSelectionModel().selectedItemProperty().addListener((o, a, sel) -> {
            if (sel != null) {
                editando = sel;
                lblForm.setText("Editando categoría");
                txtNombre.setText(sel.getNombre());
                cmbTipo.setValue(sel.getTipo());
            }
        });
        btnSave.disableProperty().bind(vm.processingProperty());
        btnDelete.disableProperty().bind(vm.processingProperty());
    }

    @FXML
    private void onNew() {
        editando = null;
        lblForm.setText("Nueva categoría");
        table.getSelectionModel().clearSelection();
        txtNombre.clear();
        cmbTipo.setValue("GASTO");
    }

    @FXML
    private void onSave() {
        if (txtNombre.getText().isBlank()) {
            Dialogs.warn("Datos incompletos", "Escribe el nombre de la categoría.");
            return;
        }
        Categoria c = new Categoria(editando == null ? null : editando.getId(), Sesion.uid(),
                txtNombre.getText().trim(), cmbTipo.getValue());
        viewModel.saveCategoria(c, editando == null ? null : editando.getNombre());
        onNew();
    }

    @FXML
    private void onDelete() {
        Categoria sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        long usos = viewModel.getMovimientos().stream().filter(m -> sel.getNombre().equals(m.getCategoria())).count();
        if (usos > 0) {
            Dialogs.warn("No se puede eliminar", "Hay " + usos + " movimiento(s) con esta categoría.");
            return;
        }
        if (Dialogs.confirm("¿Eliminar la categoría \"" + sel.getNombre() + "\"?")) {
            viewModel.deleteCategoria(sel);
            onNew();
        }
    }
}
