package com.example.coursework.fxControllers;

import com.example.coursework.HelloApplication;
import com.example.coursework.hibernateControllers.CustomHibernate;
import com.example.coursework.model.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("book_exchange_LT");
    private CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);

    public void validateAndLogin() throws IOException {
        User user = customHibernate.getUserByLoginAndPsw(loginField.getText(), passwordField.getText());
        if (user != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-form.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            Parent parent = fxmlLoader.load();
            //Ir po sio load, galiu pasiekt kontroleri
            MainForm mainForm = fxmlLoader.getController();
            mainForm.setData(entityManagerFactory, user);
            Scene scene = new Scene(parent);
            stage.setTitle("Book Exchange Platform!");
            stage.setScene(scene);
            stage.show();
        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning Login", "Wrong login or password");
        }
    }

    public void registerNewUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration-form.fxml"));

        //Stage == Window
        Stage stage = new Stage();
        Parent parent = fxmlLoader.load();
        RegistrationForm registrationForm = fxmlLoader.getController();
        registrationForm.setData(entityManagerFactory);
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange Platform!");
        stage.setScene(scene);
        //Valdo langu elgesi t.y. ar galiu dirbt su langu pries tai
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
