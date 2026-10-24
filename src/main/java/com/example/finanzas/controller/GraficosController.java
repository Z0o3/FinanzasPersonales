package com.example.finanzas.controller;

import com.example.finanzas.service.EstadisticaService;
import com.example.finanzas.util.Formato;
import com.example.finanzas.viewModel.FinanzasViewModel;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public class GraficosController {
    private static final Locale ES = new Locale("es", "MX");
    private static final String[] MESES = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    @FXML private ComboBox<Integer> cmbAnio;
    @FXML private ComboBox<Month> cmbMes;
    @FXML private PieChart pieChart;
    @FXML private LineChart<String, Number> lineChart;

    private FinanzasViewModel viewModel;

    public void initialize(FinanzasViewModel vm) {
        this.viewModel = vm;
        int y = LocalDate.now().getYear();
        cmbAnio.getItems().addAll(y, y - 1, y - 2);
        cmbAnio.setValue(y);
        cmbMes.setItems(FXCollections.observableArrayList(Month.values()));
        cmbMes.setValue(LocalDate.now().getMonth());
        cmbMes.setConverter(new StringConverter<>() {
            @Override
            public String toString(Month m) {
                return m == null ? "" : m.getDisplayName(TextStyle.FULL, ES);
            }

            @Override
            public Month fromString(String s) {
                return null;
            }
        });
        pieChart.setLabelsVisible(true);
        pieChart.setLegendSide(Side.BOTTOM);

        // Los gráficos se redibujan solos cuando cambian los datos o los selectores
        InvalidationListener refrescar = o -> actualizar();
        cmbAnio.valueProperty().addListener(refrescar);
        cmbMes.valueProperty().addListener(refrescar);
        vm.getMovimientos().addListener(refrescar);
        actualizar();
    }

    private void actualizar() {
        if (cmbAnio.getValue() == null || cmbMes.getValue() == null) return;
        int anio = cmbAnio.getValue();

        Map<String, Double> gastos = EstadisticaService.gastosPorCategoria(viewModel.getMovimientos(),
                YearMonth.of(anio, cmbMes.getValue()));
        double total = gastos.values().stream().mapToDouble(Double::doubleValue).sum();
        pieChart.getData().clear();
        gastos.forEach((cat, v) ->
                pieChart.getData().add(new PieChart.Data(String.format("%s (%.1f%%)", cat, v / total * 100), v)));
        pieChart.setTitle("Distribución de gastos – " + cmbMes.getValue().getDisplayName(TextStyle.FULL, ES)
                + " " + anio + " (" + Formato.dinero(total) + ")");

        double[][] r = EstadisticaService.ingresosVsGastos(viewModel.getMovimientos(), anio);
        XYChart.Series<String, Number> ingresos = new XYChart.Series<>();
        XYChart.Series<String, Number> gastosSerie = new XYChart.Series<>();
        ingresos.setName("Ingresos");
        gastosSerie.setName("Gastos");
        for (int i = 0; i < 12; i++) {
            ingresos.getData().add(new XYChart.Data<>(MESES[i], r[0][i]));
            gastosSerie.getData().add(new XYChart.Data<>(MESES[i], r[1][i]));
        }
        lineChart.getData().setAll(ingresos, gastosSerie);
    }
}
