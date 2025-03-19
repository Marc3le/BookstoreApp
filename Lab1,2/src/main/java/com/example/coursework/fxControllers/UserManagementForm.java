package com.example.coursework.fxControllers;

import com.example.coursework.model.Admin;
import com.example.coursework.model.Client;
import com.example.coursework.model.User;
import com.example.coursework.hibernateControllers.GenericHibernate;
import com.example.coursework.utils.DatabaseUtils;
import com.example.coursework.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import jakarta.persistence.EntityManagerFactory;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagementForm implements Initializable {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> surnameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    
    @FXML
    private TextField loginField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button newButton;
    @FXML
    private Button deleteButton;

    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;
    private User loggedInUser;
    private boolean isAdmin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue() instanceof Admin ? 
                ((Admin) cellData.getValue()).getPhoneNum() : ""));
        roleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue() instanceof Admin ? "Admin" : "Client"));

        roleComboBox.getItems().addAll("Client", "Admin");
        roleComboBox.setValue("Client");

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayUserDetails(newSelection);
            }
        });
    }

    public void setData(EntityManagerFactory entityManagerFactory, User loggedInUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
        this.loggedInUser = loggedInUser;
        this.isAdmin = loggedInUser instanceof Admin;

        // Set up visibility based on user role
        setupVisibility();
        refreshUserList();
    }

    private void setupVisibility() {
        if (!isAdmin) {
            // Regular users can only see and edit their own data
            userTable.setVisible(false);
            newButton.setVisible(false);
            deleteButton.setVisible(false);
            roleComboBox.setDisable(true);
            displayUserDetails(loggedInUser);
        }
    }

    private void refreshUserList() {
        userTable.getItems().clear();
        if (isAdmin) {
            List<User> users = genericHibernate.getAllRecords(User.class);
            userTable.getItems().addAll(users);
        } else {
            userTable.getItems().add(loggedInUser);
        }
    }

    private void displayUserDetails(User user) {
        loginField.setText(user.getLogin());
        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        emailField.setText(user.getEmail());
        phoneField.setText(user instanceof Admin ? ((Admin) user).getPhoneNum() : "");
        roleComboBox.setValue(user instanceof Admin ? "Admin" : "Client");
        passwordField.clear(); // Never display the password
    }

    @FXML
    private void handleNew() {
        if (!isAdmin) return;
        clearFields();
        userTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSave() {
        if (loginField.getText().isEmpty() || nameField.getText().isEmpty() || 
            surnameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields");
            return;
        }

        try {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            boolean isNewUser = selectedUser == null;

            if (isNewUser && !isAdmin) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "You don't have permission to create new users");
                return;
            }

            if (!isNewUser && !isAdmin && selectedUser.getId() != loggedInUser.getId()) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "You can only edit your own data");
                return;
            }

            if (isNewUser) {
                // Create new user
                User newUser = roleComboBox.getValue().equals("Admin") ? new Admin() : new Client();
                updateUserFields(newUser);
                if (!passwordField.getText().isEmpty()) {
                    newUser.setPassword(DatabaseUtils.hashPassword(passwordField.getText()));
                } else {
                    FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Password is required for new users");
                    return;
                }
                genericHibernate.create(newUser);
            } else {
                // Update existing user
                updateUserFields(selectedUser);
                if (!passwordField.getText().isEmpty()) {
                    selectedUser.setPassword(DatabaseUtils.hashPassword(passwordField.getText()));
                }
                genericHibernate.update(selectedUser);
            }

            refreshUserList();
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", 
                isNewUser ? "User created successfully" : "User updated successfully");
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to save user: " + e.getMessage());
        }
    }

    private void updateUserFields(User user) {
        user.setLogin(loginField.getText());
        user.setName(nameField.getText());
        user.setSurname(surnameField.getText());
        user.setEmail(emailField.getText());
        if (user instanceof Admin) {
            ((Admin) user).setPhoneNum(phoneField.getText());
        }
    }

    @FXML
    private void handleDelete() {
        if (!isAdmin) return;

        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete");
            return;
        }

        if (selectedUser.getId() == loggedInUser.getId()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "You cannot delete your own account");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Are you sure you want to delete this user? This action cannot be undone.", 
            ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                genericHibernate.delete(User.class, selectedUser.getId());
                refreshUserList();
                clearFields();
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully");
            } catch (Exception e) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                    "Failed to delete user: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        loginField.clear();
        nameField.clear();
        surnameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        roleComboBox.setValue("Client");
    }
} 