package com.example.coursework.fxControllers;

import javafx.scene.control.Alert;

public class FxUtils {

    public static void generateAlert(Alert.AlertType alertType, String title, String message){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
