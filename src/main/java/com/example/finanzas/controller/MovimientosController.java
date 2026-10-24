package com.example.finanzas.controller;

import com.example.finanzas.model.Movimiento;
import com.example.finanzas.util.Catalogos;
import com.example.finanzas.util.Dialogs;
import com.example.finanzas.util.Formato;
import com.example.finanzas.util.Sesion;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.beans.InvalidationListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class MovimientosController {
    @FXML private FiltroController filtroController;
    @FXML private TableView<Movimiento> table;
    @FXML private TableColumn<Movimiento, LocalDate> colFecha;
    @FXML private TableColumn<Movimiento, String> colTipo;
    @FXML private TableColumn<Movimiento, Number> colMonto;
    @FXML private TableColumn<Movimiento, String> colConcepto;
    @FXML private TableColumn<Movimiento, String> colCategoria;
    @FXML private TableColumn<Movimiento, String> colCuenta;
    @FXML private TableColumn<Movimiento, String> colEstado;
    @FXML private TableColumn<Movimiento, String> colMetodo;
    @FXML private TableColumn<Movimiento, String> colEtiquetas;

    @FXML private Label lblForm;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMonto;
    @FXML private TextField txtConcepto;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ComboBox<String> cmbCuenta;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private ComboBox<String> cmbMetodo;
    @FXML private TextField txtEtiquetas;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;
    @FXML private Label lblTotals;

    private FinanzasViewModel viewModel;
    private Movimiento editando;

    public void initialize(FinanzasViewModel vm) {
        this.viewModel = vm;

        colFecha.setCellValueFactory(d -> d.getValue().fechaProperty());
        colTipo.setCellValueFactory(d -> d.getValue().tipoProperty());
        colMonto.setCellValueFactory(d -> d.getValue().montoProperty());
        colConcepto.setCellValueFactory(d -> d.getValue().conceptoProperty());
        colCategoria.setCellValueFactory(d -> d.getValue().categoriaProperty());
        colCuenta.setCellValueFactory(d -> d.getValue().cuentaProperty());
        colEstado.setCellValueFactory(d -> d.getValue().estadoProperty());
        colMetodo.setCellValueFactory(d -> d.getValue().metodoPagoProperty());
        colEtiquetas.setCellValueFactory(d -> d.getValue().etiquetasProperty());
        colMonto.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                Movimiento m = getTableRow() == null ? null : getTableRow().getItem();
                setText(Formato.dinero(v.doubleValue()));
                setStyle(m != null && "INGRESO".equals(m.getTipo()) ? "-fx-text-fill:#2e7d32;" : "-fx-text-fill:#c62828;");
            }
        });

        FilteredList<Movimiento> filtrados = new FilteredList<>(vm.getMovimientos());
        SortedList<Movimiento> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(ordenados);
        colFecha.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(colFecha);

        // Filtros y búsqueda
        filtroController.setOnChange(() -> filtrados.setPredicate(filtroController.getFiltro()));
        vm.getCategorias().addListener((InvalidationListener) o -> {
            filtroController.setCategorias(vm.nombresCategorias(null));
            cargarCategoriasForm();
        });
        ordenados.addListener((InvalidationListener) o -> actualizarTotales(ordenados));

        // Formulario
        cmbTipo.getItems().addAll(Catalogos.TIPOS);
        cmbCuenta.getItems().addAll(Catalogos.CUENTAS);
        cmbEstado.getItems().addAll(Catalogos.ESTADOS);
        cmbMetodo.getItems().addAll(Catalogos.METODOS);
        cmbTipo.valueProperty().addListener((o, a, b) -> cargarCategoriasForm());
        table.getSelectionModel().selectedItemProperty().addListener((o, a, sel) -> {
            if (sel != null) cargarEnFormulario(sel);
        });
        btnSave.disableProperty().bind(vm.processingProperty());
        btnDelete.disableProperty().bind(vm.processingProperty());
        limpiarFormulario();
    }

    private void cargarCategoriasForm() {
        String sel = cmbCategoria.getValue();
        cmbCategoria.getItems().setAll(viewModel.nombresCategorias(cmbTipo.getValue()));
        if (sel != null && cmbCategoria.getItems().contains(sel)) cmbCategoria.setValue(sel);
    }

    private void cargarEnFormulario(Movimiento m) {
        editando = m;
        lblForm.setText("Editando movimiento");
        dpFecha.setValue(m.getFecha());
        cmbTipo.setValue(m.getTipo());
        txtMonto.setText(String.valueOf(m.getMonto()));
        txtConcepto.setText(m.getConcepto());
        cargarCategoriasForm();
        cmbCategoria.setValue(m.getCategoria());
        cmbCuenta.setValue(m.getCuenta());
        cmbEstado.setValue(m.getEstado());
        cmbMetodo.setValue(m.getMetodoPago());
        txtEtiquetas.setText(m.getEtiquetas());
    }

    private void limpiarFormulario() {
        editando = null;
        lblForm.setText("Nuevo movimiento");
        table.getSelectionModel().clearSelection();
        dpFecha.setValue(LocalDate.now());
        cmbTipo.setValue("GASTO");
        txtMonto.clear();
        txtConcepto.clear();
        cargarCategoriasForm();
        cmbCategoria.setValue(null);
        cmbCuenta.setValue(Catalogos.CUENTAS.get(0));
        cmbEstado.setValue("Aplicado");
        cmbMetodo.setValue("Efectivo");
        txtEtiquetas.clear();
    }

    private void actualizarTotales(SortedList<Movimiento> l) {
        double ing = l.stream().filter(m -> "INGRESO".equals(m.getTipo())).mapToDouble(Movimiento::getMonto).sum();
        double gas = l.stream().filter(m -> "GASTO".equals(m.getTipo())).mapToDouble(Movimiento::getMonto).sum();
        lblTotals.setText(String.format("%d movimiento(s)  |  Ingresos %s  |  Gastos %s  |  Balance %s",
                l.size(), Formato.dinero(ing), Formato.dinero(gas), Formato.dinero(ing - gas)));
    }

    @FXML
    private void onSave() {
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim().replace(",", "").replace("$", ""));
        } catch (Exception e) {
            monto = Double.NaN;
        }
        if (dpFecha.getValue() == null || Double.isNaN(monto) || monto <= 0 || txtConcepto.getText().isBlank()
                || cmbCategoria.getValue() == null || cmbCuenta.getValue() == null
                || cmbEstado.getValue() == null || cmbMetodo.getValue() == null) {
            Dialogs.warn("Datos incompletos", "Captura fecha, monto (> 0), concepto, categoría, cuenta, estado y método de pago.");
            return;
        }
        Movimiento m = new Movimiento(editando == null ? null : editando.getId(), Sesion.uid(), dpFecha.getValue(),
                cmbTipo.getValue(), monto, txtConcepto.getText().trim(), cmbCategoria.getValue(), cmbCuenta.getValue(),
                cmbEstado.getValue(), cmbMetodo.getValue(),
                String.join(" ", Movimiento.parseEtiquetas(txtEtiquetas.getText())));
        viewModel.saveMovimiento(m, msg -> Dialogs.warn("Alerta de presupuesto", msg));
        limpiarFormulario();
    }

    @FXML
    private void onNew() {
        limpiarFormulario();
    }

    @FXML
    private void onDelete() {
        Movimiento sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            Dialogs.info("Eliminar", "Selecciona un movimiento de la tabla.");
            return;
        }
        if (Dialogs.confirm("¿Eliminar \"" + sel.getConcepto() + "\"?")) {
            viewModel.deleteMovimiento(sel);
            limpiarFormulario();
        }
    }

    @FXML
    private void onReload() {
        viewModel.loadAll(null);
    }
}
