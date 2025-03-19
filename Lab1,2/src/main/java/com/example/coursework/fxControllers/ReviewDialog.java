package com.example.coursework.fxControllers;

import com.example.coursework.model.*;
import com.example.coursework.hibernateControllers.GenericHibernate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ReviewDialog implements Initializable {
    @FXML
    private TextArea reviewText;
    @FXML
    private ChoiceBox<Integer> ratingStars;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    private Publication publication;
    private User user;
    private GenericHibernate genericHibernate;
    private Runnable onReviewSubmitted;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingStars.getItems().addAll(1, 2, 3, 4, 5);
        ratingStars.setValue(5);
    }

    public void setData(Publication publication, User user, GenericHibernate genericHibernate, Runnable onReviewSubmitted) {
        this.publication = publication;
        this.user = user;
        this.genericHibernate = genericHibernate;
        this.onReviewSubmitted = onReviewSubmitted;
    }

    @FXML
    private void handleSubmit() {
        if (reviewText.getText().trim().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please enter a comment");
            return;
        }

        try {
            Rating rating = new Rating(reviewText.getText().trim(), (Client)user, publication, ratingStars.getValue());
            genericHibernate.create(rating);
            
            // Refresh the publication to get the updated comment list
            publication = genericHibernate.getEntityById(Publication.class, publication.getId());
            
            if (onReviewSubmitted != null) {
                onReviewSubmitted.run();
            }
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Review submitted successfully!");
            closeDialog();
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to submit review: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
} 