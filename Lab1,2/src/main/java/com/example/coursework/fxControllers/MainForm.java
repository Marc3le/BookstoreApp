package com.example.coursework.fxControllers;

import com.example.coursework.HelloApplication;
import com.example.coursework.hibernateControllers.GenericHibernate;
import com.example.coursework.jdbcFunctions.DatabaseUtils;
import com.example.coursework.model.*;
import com.example.coursework.model.enums.Demographic;
import com.example.coursework.model.enums.Genre;
import com.example.coursework.model.enums.Language;
import com.example.coursework.model.enums.PublicationStatus;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @FXML
    public ChoiceBox<PublicationStatus> statusFilter;
    @FXML
    public ChoiceBox<Language> languageFilter;
    @FXML
    public ChoiceBox<Genre> genreFilter;
    @FXML
    public ChoiceBox<Demographic> demographicFilter;
    @FXML
    public TextField searchField;
    @FXML
    public Button markAvailableButton;
    @FXML
    public Button markBorrowedButton;
    @FXML
    public Button markReservedButton;
    @FXML
    public Button markSoldButton;
    @FXML
    public ListView<Publication> allPublicationsListView;
    @FXML
    public TextArea selectedPublicationDetails;
    @FXML
    public TreeView<String> commentsTreeView;
    @FXML
    public RadioButton filterBookRadio;
    @FXML
    public RadioButton filterMangaRadio;
    @FXML
    public VBox bookFiltersBox;
    @FXML
    public VBox mangaFiltersBox;

    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;

    private User loggedUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        myPublicationLanguageField.getItems().addAll(Language.values());
        myPublicationGenreField.getItems().addAll(Genre.values());
        demographicField.getItems().addAll(Demographic.values());
        statusFilter.getItems().addAll(PublicationStatus.values());
        languageFilter.getItems().addAll(Language.values());
        genreFilter.getItems().addAll(Genre.values());
        demographicFilter.getItems().addAll(Demographic.values());
        
        // Initialize both publication lists
        myPublicationListField.getItems().clear();
        if (allPublicationsListView != null) {
            allPublicationsListView.getItems().clear();
            // Add selection listener for the main list view
            allPublicationsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayPublicationDetails(newValue)
            );
        }

        // Set up publication type filter visibility
        if (filterBookRadio != null && filterMangaRadio != null) {
            filterBookRadio.setSelected(true);
            updateFilterVisibility();
            filterBookRadio.setOnAction(e -> updateFilterVisibility());
            filterMangaRadio.setOnAction(e -> updateFilterVisibility());
        }

        // Add status change button listeners
        markAvailableButton.setOnAction(e -> changePublicationStatus(PublicationStatus.AVAILABLE));
        markBorrowedButton.setOnAction(e -> changePublicationStatus(PublicationStatus.BORROWED));
        markReservedButton.setOnAction(e -> changePublicationStatus(PublicationStatus.RESERVED));
        markSoldButton.setOnAction(e -> changePublicationStatus(PublicationStatus.SOLD));
    }

    public void setData(EntityManagerFactory entityManagerFactory, User user){
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
        this.loggedUser = user;
        setUserVisibility();
        
        // Add listeners for filters after genericHibernate is initialized
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        languageFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        genreFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        demographicFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        
        refreshPublicationList();
    }

    private void setUserVisibility() {
        if(loggedUser instanceof Client){
            // Clients can add publications but cannot delete them
            myPublicationBookRadio.setDisable(false);
            myPublicationMangaRadio.setDisable(false);
            myPublicationTitleField.setDisable(false);
            myPublicationDescriptionField.setDisable(false);
            myPublicationAuthorField.setDisable(false);
            myPublicationIsbnField.setDisable(false);
            mysPublicationYearField.setDisable(false);
            myPublicationLanguageField.setDisable(false);
            myPublicationGenreField.setDisable(false);
            demographicField.setDisable(false);
            illustratorField.setDisable(false);
            volumeField.setDisable(false);
            isMangaColoredField.setDisable(false);
        } else {
            // Admin has full access to all fields
            myPublicationBookRadio.setDisable(false);
            myPublicationMangaRadio.setDisable(false);
            myPublicationTitleField.setDisable(false);
            myPublicationDescriptionField.setDisable(false);
            myPublicationAuthorField.setDisable(false);
            myPublicationIsbnField.setDisable(false);
            mysPublicationYearField.setDisable(false);
            myPublicationLanguageField.setDisable(false);
            myPublicationGenreField.setDisable(false);
            demographicField.setDisable(false);
            illustratorField.setDisable(false);
            volumeField.setDisable(false);
            isMangaColoredField.setDisable(false);
        }
    }

    public void createNewPublication() {
        if (myPublicationBookRadio.isSelected()) {
            if (validateBookFields()) {
                try {
                    Book book = new Book(myPublicationTitleField.getText(),
                            myPublicationDescriptionField.getText(),
                            myPublicationLanguageField.getValue(),
                            myPublicationIsbnField.getText(),
                            mysPublicationYearField.getValue().getYear(),
                            myPublicationAuthorField.getText(),
                            myPublicationGenreField.getValue());
                    book.setOwner(loggedUser);

                    genericHibernate.create(book);
                    refreshPublicationList();
                    FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Book created successfully!");
                    clearAllFields();
                } catch (Exception e) {
                    FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to create book: " + e.getMessage());
                }
            }
        } else {
            if (validateMangaFields()) {
                try {
                    Manga manga = new Manga(
                        myPublicationTitleField.getText(),
                        myPublicationDescriptionField.getText(),
                        myPublicationLanguageField.getValue(),
                        demographicField.getValue(),
                        illustratorField.getText(),
                        Integer.parseInt(volumeField.getText()),
                        isMangaColoredField.isSelected()
                    );
                    manga.setOwner(loggedUser);

                    genericHibernate.create(manga);
                    refreshPublicationList();
                    FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Manga created successfully!");
                    clearAllFields();
                } catch (Exception e) {
                    FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to create manga: " + e.getMessage());
                }
            }
        }
    }

    private boolean validateBookFields() {
        if (myPublicationTitleField.getText().isEmpty() || 
            myPublicationDescriptionField.getText().isEmpty() ||
            myPublicationLanguageField.getValue() == null ||
            myPublicationIsbnField.getText().isEmpty() ||
            mysPublicationYearField.getValue() == null ||
            myPublicationAuthorField.getText().isEmpty() ||
            myPublicationGenreField.getValue() == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields");
            return false;
        }
        return true;
    }

    private boolean validateMangaFields() {
        if (myPublicationTitleField.getText().isEmpty() || 
            myPublicationDescriptionField.getText().isEmpty() ||
            myPublicationLanguageField.getValue() == null ||
            illustratorField.getText().isEmpty() ||
            demographicField.getValue() == null ||
            volumeField.getText().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields");
            return false;
        }
        try {
            Integer.parseInt(volumeField.getText());
        } catch (NumberFormatException e) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Volume must be a number");
            return false;
        }
        return true;
    }

    private void refreshPublicationList() {
        myPublicationListField.getItems().clear();
        List<Publication> allPublications = genericHibernate.getAllRecords(Publication.class);
        // Filter publications to show only those belonging to the logged-in user
        List<Publication> userPublications = allPublications.stream()
                .filter(pub -> pub.getOwner() != null && pub.getOwner().getId() == loggedUser.getId())
                .collect(Collectors.toList());
        myPublicationListField.getItems().addAll(userPublications);
        
        // Also refresh the main page publications list
        refreshAllPublicationsList();
    }

    private void refreshAllPublicationsList() {
        if (allPublicationsListView != null) {
            allPublicationsListView.getItems().clear();
            allPublicationsListView.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
        }
    }

    private void updateFilterVisibility() {
        if (bookFiltersBox != null && mangaFiltersBox != null) {
            bookFiltersBox.setVisible(filterBookRadio.isSelected());
            bookFiltersBox.setManaged(filterBookRadio.isSelected());
            mangaFiltersBox.setVisible(filterMangaRadio.isSelected());
            mangaFiltersBox.setManaged(filterMangaRadio.isSelected());
            if (genericHibernate != null) {
                applyFilters();
            }
        }
    }

    private void applyFilters() {
        List<Publication> allPublications = genericHibernate.getAllRecords(Publication.class);
        List<Publication> filteredPublications = allPublications.stream()
                .filter(pub -> {
                    if (filterBookRadio.isSelected()) {
                        return pub instanceof Book;
                    } else {
                        return pub instanceof Manga;
                    }
                })
                .filter(pub -> statusFilter.getValue() == null || pub.getStatus() == statusFilter.getValue())
                .filter(pub -> languageFilter.getValue() == null || pub.getLanguage() == languageFilter.getValue())
                .filter(pub -> {
                    if (filterBookRadio.isSelected() && genreFilter.getValue() != null) {
                        return pub instanceof Book && ((Book) pub).getGenre() == genreFilter.getValue();
                    }
                    return true;
                })
                .filter(pub -> {
                    if (filterMangaRadio.isSelected() && demographicFilter.getValue() != null) {
                        return pub instanceof Manga && ((Manga) pub).getDemographic() == demographicFilter.getValue();
                    }
                    return true;
                })
                .filter(pub -> {
                    if (searchField.getText().isEmpty()) return true;
                    String searchLower = searchField.getText().toLowerCase();
                    return pub.getTitle().toLowerCase().contains(searchLower) ||
                           pub.getDescription().toLowerCase().contains(searchLower);
                })
                .collect(Collectors.toList());

        // Update both lists
        if (allPublicationsListView != null) {
            allPublicationsListView.getItems().clear();
            allPublicationsListView.getItems().addAll(filteredPublications);
        }
    }

    public void disableFields() {
        if (loggedUser instanceof Client) {
            return; // Don't modify fields for clients as they're already disabled in setUserVisibility
        }

        if (myPublicationMangaRadio.isSelected()) {
            myPublicationIsbnField.setDisable(true);
            myPublicationAuthorField.setDisable(true);
            myPublicationGenreField.setDisable(true);
            mysPublicationYearField.setDisable(true);
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
        if (publication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication first");
            return;
        }
        
        clearAllFields();
        myPublicationTitleField.setText(publication.getTitle());
        myPublicationDescriptionField.setText(publication.getDescription());
        myPublicationLanguageField.setValue(publication.getLanguage());
        
        if (publication instanceof Book book) {
            myPublicationBookRadio.setSelected(true);
            myPublicationIsbnField.setText(book.getIsbn());
            myPublicationAuthorField.setText(book.getAuthors());
            mysPublicationYearField.setValue(java.time.LocalDate.of(book.getYear(), 1, 1));
            myPublicationGenreField.setValue(book.getGenre());
            disableFields();
        } else if (publication instanceof Manga manga) {
            myPublicationMangaRadio.setSelected(true);
            illustratorField.setText(manga.getIllustrator());
            demographicField.setValue(manga.getDemographic());
            volumeField.setText(String.valueOf(manga.getVolume()));
            isMangaColoredField.setSelected(manga.isColored());
            disableFields();
        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Unknown publication type");
        }
    }

    private void clearAllFields() {
        myPublicationTitleField.clear();
        myPublicationDescriptionField.clear();
        myPublicationAuthorField.clear();
        myPublicationIsbnField.clear();
        mysPublicationYearField.getEditor().clear();
        myPublicationLanguageField.setValue(null);
        myPublicationGenreField.setValue(null);
        demographicField.setValue(null);
        illustratorField.clear();
        volumeField.clear();
        isMangaColoredField.setSelected(false);
    }

    public void updatePublication() {
        Publication publication = myPublicationListField.getSelectionModel().getSelectedItem();
        if (publication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication to update");
            return;
        }

        try {
            if (myPublicationBookRadio.isSelected()) {
                if (validateBookFields()) {
                    Book book = (Book) publication;
                    book.setTitle(myPublicationTitleField.getText());
                    book.setDescription(myPublicationDescriptionField.getText());
                    book.setLanguage(myPublicationLanguageField.getValue());
                    book.setIsbn(myPublicationIsbnField.getText());
                    book.setYear(mysPublicationYearField.getValue().getYear());
                    book.setAuthors(myPublicationAuthorField.getText());
                    book.setGenre(myPublicationGenreField.getValue());

                    genericHibernate.update(book);
                    refreshPublicationList();
                    FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
                    clearAllFields();
                }
            } else {
                if (validateMangaFields()) {
                    Manga manga = (Manga) publication;
                    manga.setTitle(myPublicationTitleField.getText());
                    manga.setDescription(myPublicationDescriptionField.getText());
                    manga.setLanguage(myPublicationLanguageField.getValue());
                    manga.setDemographic(demographicField.getValue());
                    manga.setIllustrator(illustratorField.getText());
                    manga.setVolume(Integer.parseInt(volumeField.getText()));
                    manga.setColored(isMangaColoredField.isSelected());

                    genericHibernate.update(manga);
                    refreshPublicationList();
                    FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Manga updated successfully!");
                    clearAllFields();
                }
            }
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to update publication: " + e.getMessage());
        }
    }

    public void deletePublication() {
        Publication publication = myPublicationListField.getSelectionModel().getSelectedItem();
        Publication publication1 = genericHibernate.getEntityById(Publication.class, publication.getId());
        //genericHibernate.delete(publication1); Why detached sioje vietoje
        genericHibernate.delete(Publication.class, publication.getId());
        myPublicationListField.getItems().clear();
        myPublicationListField.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
    }

    public void loadRegForm(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration-form.fxml"));
            if (fxmlLoader.getLocation() == null) {
                throw new IOException("Registration form FXML not found");
            }

            Stage stage = new Stage();
            Parent parent = fxmlLoader.load();
            RegistrationForm registrationForm = fxmlLoader.getController();
            registrationForm.setData(entityManagerFactory);
            
            Scene scene = new Scene(parent);
            stage.setTitle("Book Exchange Platform!");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to load registration form: " + e.getMessage());
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void changePublicationStatus(PublicationStatus newStatus) {
        Publication publication = myPublicationListField.getSelectionModel().getSelectedItem();
        if (publication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication first");
            return;
        }

        try {
            publication.setStatus(newStatus);
            genericHibernate.update(publication);
            refreshPublicationList();
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Publication status updated successfully!");
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to update publication status: " + e.getMessage());
        }
    }

    @FXML
    public void leaveReview() {
        // TODO: Implement review functionality
        FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Info", "Review functionality coming soon!");
    }

    @FXML
    public void requestPublication() {
        // TODO: Implement request functionality
        FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Info", "Request functionality coming soon!");
    }

    private void displayPublicationDetails(Publication publication) {
        if (publication == null || selectedPublicationDetails == null) {
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Title: ").append(publication.getTitle()).append("\n\n");
        details.append("Description: ").append(publication.getDescription()).append("\n\n");
        details.append("Language: ").append(publication.getLanguage()).append("\n");
        details.append("Status: ").append(publication.getStatus()).append("\n");
        details.append("Date Created: ").append(publication.getDateCreated()).append("\n\n");

        if (publication instanceof Book book) {
            details.append("Type: Book\n");
            details.append("ISBN: ").append(book.getIsbn()).append("\n");
            details.append("Authors: ").append(book.getAuthors()).append("\n");
            details.append("Year: ").append(book.getYear()).append("\n");
            details.append("Genre: ").append(book.getGenre());
        } else if (publication instanceof Manga manga) {
            details.append("Type: Manga\n");
            details.append("Illustrator: ").append(manga.getIllustrator()).append("\n");
            details.append("Volume: ").append(manga.getVolume()).append("\n");
            details.append("Demographic: ").append(manga.getDemographic()).append("\n");
            details.append("Colored: ").append(manga.isColored() ? "Yes" : "No");
        }

        selectedPublicationDetails.setText(details.toString());
        
        // TODO: Update comments tree view
        if (commentsTreeView != null) {
            commentsTreeView.setRoot(null); // Clear comments for now
        }
    }
}
