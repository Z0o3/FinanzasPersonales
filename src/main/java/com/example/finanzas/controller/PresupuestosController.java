package com.example.finanzas.controller;

import com.example.finanzas.model.Presupuesto;
import com.example.finanzas.model.PresupuestoResumen;
import com.example.finanzas.service.EstadisticaService;
import com.example.finanzas.util.Dialogs;
import com.example.finanzas.util.Formato;
import com.example.finanzas.util.Sesion;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Topes de gasto mensual por categoría; la barra cambia de color al acercarse (ámbar) o rebasar (rojo). */
public class PresupuestosController {
    @FXML private TableView<PresupuestoResumen> table;
    @FXML private TableColumn<PresupuestoResumen, String> colCategoria;
    @FXML private TableColumn<PresupuestoResumen, Double> colLimite;
    @FXML private TableColumn<PresupuestoResumen, Double> colGastado;
    @FXML private TableColumn<PresupuestoResumen, Double> colDisponible;
    @FXML private TableColumn<PresupuestoResumen, Double> colAvance;
    @FXML private Label lblForm;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtLimite;
    @FXML private TextField txtUmbral;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;

    private FinanzasViewModel viewModel;
    private Presupuesto editando;

    public void initialize(FinanzasViewModel vm) {
        this.viewModel = vm;

        colCategoria.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCategoria()));
        dineroColumn(colLimite, PresupuestoResumen::getLimite);
        dineroColumn(colGastado, PresupuestoResumen::getGastado);
        dineroColumn(colDisponible, PresupuestoResumen::getDisponible);
        colAvance.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPorcentaje()));
        colAvance.setCellFactory(c -> new TableCell<>() {
            private final ProgressBar pb = new ProgressBar();

            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                pb.setProgress(Math.min(v, 1));
                String color = v >= 1 ? "#d32f2f" : v >= 0.8 ? "#f9a825" : "#388e3c";
                pb.setStyle("-fx-accent:" + color + ";");
                setGraphic(pb);
                setText(String.format("%.0f%%", v * 100));
            }
        });

        // Se recalcula cuando cambian presupuestos o movimientos
        InvalidationListener refrescar = o -> refrescarTabla();
        vm.getPresupuestos().addListener(refrescar);
        vm.getMovimientos().addListener(refrescar);
        vm.getCategorias().addListener((InvalidationListener) o ->
                cmbCategoria.getItems().setAll(vm.nombresCategorias("GASTO")));

        table.getSelectionModel().selectedItemProperty().addListener((o, a, sel) -> {
            if (sel != null) cargarEnFormulario(sel.getPresupuesto());
        });
        btnSave.disableProperty().bind(vm.processingProperty());
        btnDelete.disableProperty().bind(vm.processingProperty());
    }

    private void dineroColumn(TableColumn<PresupuestoResumen, Double> col, Function<PresupuestoResumen, Double> getter) {
        col.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(getter.apply(d.getValue())));
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : Formato.dinero(v));
            }
        });
    }

    private void refrescarTabla() {
        YearMonth ym = YearMonth.now();
        List<PresupuestoResumen> filas = viewModel.getPresupuestos().stream()
                .map(p -> new PresupuestoResumen(p, EstadisticaService.gastoMes(viewModel.getMovimientos(), p.getCategoria(), ym)))
                .collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(filas));
    }

    private void cargarEnFormulario(Presupuesto p) {
        editando = p;
        lblForm.setText("Editando presupuesto");
        cmbCategoria.setValue(p.getCategoria());
        txtLimite.setText(String.valueOf(p.getLimiteMensual()));
        txtUmbral.setText(String.valueOf((int) Math.round(p.getUmbralAviso() * 100)));
    }

    @FXML
    private void onNew() {
        editando = null;
        lblForm.setText("Nuevo presupuesto");
        table.getSelectionModel().clearSelection();
        cmbCategoria.setValue(null);
        txtLimite.clear();
        txtUmbral.setText("80");
    }

    @FXML
    private void onSave() {
        try {
            double limite = Double.parseDouble(txtLimite.getText().trim().replace(",", ""));
            double umbral = Double.parseDouble(txtUmbral.getText().trim());
            if (cmbCategoria.getValue() == null || limite <= 0 || umbral <= 0 || umbral > 100) throw new IllegalArgumentException();
            Presupuesto p = new Presupuesto(editando == null ? null : editando.getId(), Sesion.uid(),
                    cmbCategoria.getValue(), limite, umbral / 100.0);
            viewModel.savePresupuesto(p);
            onNew();
        } catch (Exception e) {
            Dialogs.warn("Datos inválidos", "Elige una categoría, un límite mayor a 0 y un porcentaje entre 1 y 100.");
        }
    }

    @FXML
    private void onDelete() {
        PresupuestoResumen sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (Dialogs.confirm("¿Eliminar el presupuesto de " + sel.getCategoria() + "?")) {
            viewModel.deletePresupuesto(sel.getPresupuesto());
            onNew();
        }
    }
}
