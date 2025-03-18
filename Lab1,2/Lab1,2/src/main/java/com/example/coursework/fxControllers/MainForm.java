package com.example.coursework.fxControllers;

import com.example.coursework.HelloApplication;
import com.example.coursework.hibernateControllers.GenericHibernate;
import com.example.coursework.jdbcFunctions.DatabaseUtils;
import com.example.coursework.model.*;
import com.example.coursework.model.enums.Demographic;
import com.example.coursework.model.enums.Genre;
import com.example.coursework.model.enums.Language;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainForm implements Initializable {
    @FXML
    public ListView<Publication> myPublicationListField;
    @FXML
    public RadioButton myPublicationBookRadio;
    @FXML
    public RadioButton myPublicationMangaRadio;
    @FXML
    public TextField myPublicationTitleField;
    @FXML
    public TextArea myPublicationDescriptionField;
    @FXML
    public TextArea myPublicationAuthorField;
    @FXML
    public TextField myPublicationIsbnField;
    @FXML
    public DatePicker mysPublicationYearField;
    @FXML
    public ChoiceBox<Language> myPublicationLanguageField;
    @FXML
    public ChoiceBox<Genre> myPublicationGenreField;
    @FXML
    public ChoiceBox<Demographic> demographicField;
    @FXML
    public TextField illustratorField;
    @FXML
    public TextField volumeField;
    public CheckBox isMangaColoredField;

    private EntityManagerFactory entityManagerFactory; //= Persistence.createEntityManagerFactory("book_exchange_LT");
    private GenericHibernate genericHibernate = new GenericHibernate(entityManagerFactory);

    private User loggedUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user){
        this.entityManagerFactory = entityManagerFactory;
        this.loggedUser = user;
        setUserVisibility();
    }

    private void setUserVisibility() {
        if(loggedUser instanceof Client){
            //slepsiu laukus
        }else {
            //admino funkcionalumas

        }
    }

    public void createNewPublication() {
        //Cia reikia atlikti patikrinima ar visi reikiami laukai uzpildyti

        if (myPublicationBookRadio.isSelected()) {
            //TODO visus laukus privalomus reik patikrint
            if (!myPublicationTitleField.getText().isEmpty() && !myPublicationDescriptionField.getText().isEmpty()) {
                Book book = new Book(myPublicationTitleField.getText(),
                        myPublicationDescriptionField.getText(),
                        myPublicationLanguageField.getValue(),
                        myPublicationIsbnField.getText(),
                        mysPublicationYearField.getValue().getYear(),
                        myPublicationAuthorField.getText(),
                        myPublicationGenreField.getValue());

                genericHibernate.create(book);
                myPublicationListField.getItems().clear();
                myPublicationListField.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
            } else {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning alert", "Please fill in all required fields");
            }
        } else {
            //TODO sukurti manga objekta

            myPublicationListField.getItems().clear();
            myPublicationListField.getItems().addAll(genericHibernate.getAllRecords(Publication.class));

        }


        //Noriu iskviesti pranesima, kad viskas pavyko
        //FxUtils.generateAlert(Alert.AlertType.WARNING, "Info alert", "Publication created successfully");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        myPublicationLanguageField.getItems().addAll(Language.values());
        myPublicationGenreField.getItems().addAll(Genre.values());
        demographicField.getItems().addAll(Demographic.values());
        myPublicationListField.getItems().clear();
        myPublicationListField.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
    }

    public void disableFields() {
        if (myPublicationMangaRadio.isSelected()) {
            myPublicationIsbnField.setDisable(true);
            myPublicationAuthorField.setDisable(true);
            myPublicationGenreField.setDisable(true);
            mysPublicationYearField.setDisable(true);
            //Isvis nerodyti formoje. Ne visi grafiniai elementai turi sita galimybe
            //mysPublicationYearField.setVisible(false);
            illustratorField.setDisable(false);
            demographicField.setDisable(false);
            volumeField.setDisable(false);
        } else {
            myPublicationIsbnField.setDisable(false);
            myPublicationAuthorField.setDisable(false);
            myPublicationGenreField.setDisable(false);
            mysPublicationYearField.setDisable(false);
            illustratorField.setDisable(true);
            demographicField.setDisable(true);
            volumeField.setDisable(true);
        }
    }

    public void loadSelectedPublication() {
        Publication publication = myPublicationListField.getSelectionModel().getSelectedItem();
        clearAllFields();
        myPublicationTitleField.setText(publication.getTitle());
        myPublicationDescriptionField.setText(publication.getDescription());
        if (publication instanceof Book book) {
            myPublicationBookRadio.setSelected(true);
            myPublicationIsbnField.setText(book.getIsbn());
            myPublicationAuthorField.setText(book.getAuthors());
            //TODO likusius tik knygos laukus pabaigti
        } else if (publication instanceof Manga manga) {
            myPublicationMangaRadio.setSelected(true);
            volumeField.setText(String.valueOf(manga.getVolume()));


        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning alert", "Eh? What is this?");
        }


    }

    private void clearAllFields() {
        myPublicationTitleField.clear();
        myPublicationDescriptionField.clear();
        myPublicationAuthorField.clear();
        myPublicationIsbnField.clear();
        mysPublicationYearField.getEditor().clear();
        //TODO visiems laukams

    }

    public void updatePublication() {


    }

    public void deletePublication() {
        Publication publication = myPublicationListField.getSelectionModel().getSelectedItem();
        Publication publication1 = genericHibernate.getEntityById(Publication.class, publication.getId());
        //genericHibernate.delete(publication1); Why detached sioje vietoje
        genericHibernate.delete(Publication.class, publication.getId());
        myPublicationListField.getItems().clear();
        myPublicationListField.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
    }

    public void loadRegForm(ActionEvent actionEvent) throws IOException {
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
