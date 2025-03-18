package com.example.coursework.fxControllers;

import com.example.coursework.model.Admin;
import com.example.coursework.model.Client;
import com.example.coursework.model.User;
import com.example.coursework.hibernateControllers.CustomHibernate;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistrationForm {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker birthDateField;
    @FXML
    public TextField phoneField;
    @FXML
    public Label titleLabel;
    @FXML
    public RadioButton clientRadio;
    @FXML
    public RadioButton adminRadio;
    @FXML
    public ToggleGroup userType;

    private EntityManagerFactory entityManagerFactory;
    private boolean isClient = true;

    public void setData(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        setupForm();
    }

    @FXML
    public void onUserTypeChanged() {
        isClient = clientRadio.isSelected();
        setupForm();
    }

    private void setupForm() {
        if (isClient) {
            titleLabel.setText("Register New Client");
            phoneField.setVisible(false);
            addressField.setVisible(true);
            birthDateField.setVisible(true);
        } else {
            titleLabel.setText("Register New Admin");
            phoneField.setVisible(true);
            addressField.setVisible(false);
            birthDateField.setVisible(false);
        }
    }

    public void registerUser() {
        if (validateFields()) {
            User user;
            if (isClient) {
                user = new Client(
                    loginField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    birthDateField.getValue()
                );
            } else {
                Admin admin = new Admin(
                    loginField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText()
                );
                admin.setPhoneNum(phoneField.getText());
                user = admin;
            }

            try {
                new CustomHibernate(entityManagerFactory).create(user);
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
                closeWindow();
            } catch (Exception e) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to register user. Error: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() ||
            nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
            emailField.getText().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields");
            return false;
        }

        if (isClient && (addressField.getText().isEmpty() || birthDateField.getValue() == null)) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in address and birth date");
            return false;
        }

        if (!isClient && phoneField.getText().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in phone number");
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.close();
    }
}
