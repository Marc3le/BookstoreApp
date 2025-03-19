package com.example.coursework.fxControllers;

import com.example.coursework.model.PublicationRequest;
import com.example.coursework.model.enums.PublicationStatus;
import com.example.coursework.hibernateControllers.GenericHibernate;
import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RequestListDialog implements Initializable {
    @FXML
    private VBox requestsContainer;
    @FXML
    private Button closeButton;

    private List<PublicationRequest> requests;
    private GenericHibernate genericHibernate;
    private Runnable onRequestsProcessed;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setData(List<PublicationRequest> requests, GenericHibernate genericHibernate, Runnable onRequestsProcessed) {
        this.requests = requests;
        this.genericHibernate = genericHibernate;
        this.onRequestsProcessed = onRequestsProcessed;
        displayRequests();
    }

    private void displayRequests() {
        requestsContainer.getChildren().clear();
        for (PublicationRequest request : requests) {
            HBox requestBox = new HBox(10);
            Label requestInfo = new Label(String.format("User: %s, Requested at: %s",
                request.getRequester().getLogin(),
                request.getRequestedAt().toLocalDate().toString()));

            Button acceptButton = new Button("Accept");
            acceptButton.setOnAction(e -> handleAccept(request));

            Button declineButton = new Button("Decline");
            declineButton.setOnAction(e -> handleDecline(request));

            requestBox.getChildren().addAll(requestInfo, acceptButton, declineButton);
            requestsContainer.getChildren().add(requestBox);
        }
    }

    private void handleAccept(PublicationRequest request) {
        try {
            EntityManager em = genericHibernate.getEntityManager();
            em.getTransaction().begin();
            
            request.setStatus(PublicationRequest.RequestStatus.ACCEPTED);
            request.getPublication().setStatus(PublicationStatus.BORROWED);
            em.merge(request);
            em.merge(request.getPublication());
            
            // Decline all other pending requests for this publication
            List<PublicationRequest> otherRequests = em.createQuery(
                "SELECT r FROM PublicationRequest r WHERE r.publication = :pub AND r.status = :status AND r.id != :currentId",
                PublicationRequest.class)
                .setParameter("pub", request.getPublication())
                .setParameter("status", PublicationRequest.RequestStatus.PENDING)
                .setParameter("currentId", request.getId())
                .getResultList();

            for (PublicationRequest otherRequest : otherRequests) {
                otherRequest.setStatus(PublicationRequest.RequestStatus.DECLINED);
                em.merge(otherRequest);
            }

            em.getTransaction().commit();
            closeDialog();
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to accept request: " + e.getMessage());
        }
    }

    private void handleDecline(PublicationRequest request) {
        try {
            EntityManager em = genericHibernate.getEntityManager();
            em.getTransaction().begin();
            
            request.setStatus(PublicationRequest.RequestStatus.DECLINED);
            em.merge(request);
            
            em.getTransaction().commit();
            closeDialog();
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to decline request: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void closeDialog() {
        if (onRequestsProcessed != null) {
            onRequestsProcessed.run();
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
} 