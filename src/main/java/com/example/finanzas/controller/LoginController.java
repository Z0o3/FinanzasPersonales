package com.example.finanzas.controller;

import com.example.finanzas.viewModel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Label lblStatus;
    @FXML private StackPane loadingOverlay;

    private LoginViewModel viewModel;
    private Runnable onSuccess;

    public void initialize(LoginViewModel vm, Runnable onSuccess) {
        this.viewModel = vm;
        this.onSuccess = onSuccess;
        loadingOverlay.visibleProperty().bind(vm.processingProperty());
        btnLogin.disableProperty().bind(vm.readyProperty().not().or(vm.processingProperty()));
        btnRegister.disableProperty().bind(vm.readyProperty().not().or(vm.processingProperty()));
        lblStatus.textProperty().bind(vm.statusProperty());
        vm.prepare();
    }

    @FXML
    private void onLogin() {
        viewModel.login(txtUser.getText(), txtPass.getText(), onSuccess);
    }

    @FXML
    private void onRegister() {
        viewModel.register(txtUser.getText(), txtPass.getText(), onSuccess);
    }
}
