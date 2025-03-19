package com.example.coursework.fxControllers;

import com.example.coursework.HelloApplication;
import com.example.coursework.hibernateControllers.GenericHibernate;
import com.example.coursework.hibernateControllers.CustomHibernate;
import com.example.coursework.utils.DatabaseUtils;
import com.example.coursework.utils.FxUtils;
import com.example.coursework.model.*;
import com.example.coursework.model.enums.Demographic;
import com.example.coursework.model.enums.Genre;
import com.example.coursework.model.enums.Language;
import com.example.coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainForm implements Initializable {
    @FXML
    public ListView<Publication> myPublicationListField;
    @FXML
    public ListView<Publication> allPublicationsListView;
    @FXML
    public TextArea selectedPublicationDetails;
    @FXML
    public TreeView<String> commentsTreeView;
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
    @FXML
    public CheckBox isMangaColoredField;
    @FXML
    public RadioButton filterBookRadio;
    @FXML
    public RadioButton filterMangaRadio;
    @FXML
    public VBox bookFiltersBox;
    @FXML
    public VBox mangaFiltersBox;
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
    public Button editCommentButton;
    @FXML
    public Button deleteCommentButton;
    @FXML
    public TextArea editCommentText;

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
    private Button saveButton;
    @FXML
    private Button deleteButton;

    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;
    private CustomHibernate customHibernate;

    private User loggedUser;
    private boolean isAdmin;

    private Comment selectedComment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBoxes();
        initializePublicationLists();
        initializeRadioButtons();
        initializeStatusButtons();
        initializeUserManagement();
        initializeCommentManagement();
    }

    private void initializeChoiceBoxes() {
        myPublicationLanguageField.getItems().addAll(Language.values());
        myPublicationGenreField.getItems().addAll(Genre.values());
        demographicField.getItems().addAll(Demographic.values());
        statusFilter.getItems().addAll(PublicationStatus.AVAILABLE, PublicationStatus.BORROWED);
        languageFilter.getItems().addAll(Language.values());
        genreFilter.getItems().addAll(Genre.values());
        demographicFilter.getItems().addAll(Demographic.values());
    }

    private void initializePublicationLists() {
        myPublicationListField.getItems().clear();
        if (allPublicationsListView != null) {
            allPublicationsListView.getItems().clear();
            allPublicationsListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> displayPublicationDetails(newValue)
            );
        }
    }

    private void initializeRadioButtons() {
        EventHandler<ActionEvent> disableHandler = e -> disableFields();
        myPublicationBookRadio.setOnAction(disableHandler);
        myPublicationMangaRadio.setOnAction(disableHandler);

        if (filterBookRadio != null && filterMangaRadio != null) {
            filterBookRadio.setSelected(true);
            updateFilterVisibility();
            filterBookRadio.setOnAction(e -> updateFilterVisibility());
            filterMangaRadio.setOnAction(e -> updateFilterVisibility());
        }
    }

    private void initializeStatusButtons() {
        markAvailableButton.setOnAction(e -> changePublicationStatus(PublicationStatus.AVAILABLE));
        markBorrowedButton.setOnAction(e -> changePublicationStatus(PublicationStatus.BORROWED));
        markReservedButton.setVisible(false);
        markSoldButton.setVisible(false);
        disableFields();
    }

    private void initializeUserManagement() {
        if (userTable != null) {
            loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneColumn.setCellValueFactory(data -> 
                new SimpleStringProperty(data.getValue() instanceof Admin ? 
                    ((Admin) data.getValue()).getPhoneNum() : ""));
            roleColumn.setCellValueFactory(data -> 
                new SimpleStringProperty(data.getValue() instanceof Admin ? "Admin" : "Client"));

            roleComboBox.getItems().addAll("Client", "Admin");
            roleComboBox.setValue("Client");

            userTable.getSelectionModel().selectedItemProperty().addListener((_, _, user) -> {
                if (user != null) displayUserDetails(user);
            });
        }
    }

    private void initializeCommentManagement() {
        if (commentsTreeView != null) {
            editCommentButton.setVisible(false);
            deleteCommentButton.setVisible(false);
            editCommentText.setVisible(false);
            
            commentsTreeView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
                if (newValue != null) {
                    selectedComment = findCommentByTreeItem(newValue);
                    updateCommentButtons();
                }
            });
        }
    }

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
        this.customHibernate = new CustomHibernate(entityManagerFactory);
        this.loggedUser = user;
        this.isAdmin = user instanceof Admin;
        
        setupUserAccess();
        setupFilters();
        refreshLists();
    }

    private void setupUserAccess() {
        setUserVisibility();
    }

    private void setupFilters() {
        ChangeListener<Object> filterListener = (_, _, _) -> applyFilters();
        statusFilter.valueProperty().addListener(filterListener);
        languageFilter.valueProperty().addListener(filterListener);
        genreFilter.valueProperty().addListener(filterListener);
        demographicFilter.valueProperty().addListener(filterListener);
        searchField.textProperty().addListener(filterListener);
    }

    private void refreshLists() {
        refreshPublicationList();
        refreshUserList();
    }

    private void setUserVisibility() {
        boolean isClient = loggedUser instanceof Client;
        
        // Hide user management controls for non-admin users
        if (userTable != null) {
            // For admin: show all users and add button
            // For regular users: show only themselves
            userTable.setVisible(true);
            userTable.setManaged(true);
            
            if (isAdmin) {
                // Admin sees all controls and all users
                saveButton.setVisible(true);
                deleteButton.setVisible(true);
                newButton.setText("Add User");
                newButton.setVisible(true);
                refreshUserList();
            } else {
                // Regular users only see their own info and can edit it
                saveButton.setVisible(true);
                deleteButton.setVisible(false);
                newButton.setVisible(false);
                
                // Show only the logged-in user
                userTable.getItems().clear();
                userTable.getItems().add(loggedUser);
                
                // Select the user's row
                userTable.getSelectionModel().select(loggedUser);
                displayUserDetails(loggedUser);
            }
            
            // Set field editability
            boolean canEdit = isAdmin || !loginField.getText().isEmpty();
            loginField.setDisable(true); // Login should never be editable
            nameField.setDisable(!canEdit);
            surnameField.setDisable(!canEdit);
            emailField.setDisable(!canEdit);
            phoneField.setDisable(!canEdit);
            roleComboBox.setDisable(!isAdmin); // Only admins can change roles
            passwordField.setDisable(!canEdit);
        }
    }

    private void setFieldsDisabled(boolean disabled) {
        myPublicationBookRadio.setDisable(disabled);
        myPublicationMangaRadio.setDisable(disabled);
        myPublicationTitleField.setDisable(disabled);
        myPublicationDescriptionField.setDisable(disabled);
        myPublicationAuthorField.setDisable(disabled);
        myPublicationIsbnField.setDisable(disabled);
        mysPublicationYearField.setDisable(disabled);
        myPublicationLanguageField.setDisable(disabled);
        myPublicationGenreField.setDisable(disabled);
        demographicField.setDisable(disabled);
        illustratorField.setDisable(disabled);
        volumeField.setDisable(disabled);
        isMangaColoredField.setDisable(disabled);
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
                    book.setDateCreated(java.time.LocalDate.now());

                genericHibernate.create(book);
                    refreshPublicationList();
                    FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Book created successfully!");
                    clearAllFields();
                } catch (Exception e) {
                    FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to create book: " + e.getMessage());
                }
            }
        } else if (myPublicationMangaRadio.isSelected()) {
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
                    manga.setDateCreated(java.time.LocalDate.now());

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
        if (myPublicationListField != null && genericHibernate != null) {
            List<Publication> allPublications = genericHibernate.getAllRecords(Publication.class);

            myPublicationListField.getItems().clear();
            myPublicationListField.getItems().addAll(
                allPublications.stream()
                    .filter(pub -> pub.getOwner() != null && pub.getOwner().getId() == loggedUser.getId())
                    .collect(Collectors.toList())
            );
            
            refreshAllPublicationsList();
        }
    }

    private void refreshAllPublicationsList() {
        if (allPublicationsListView != null && genericHibernate != null) {
            allPublicationsListView.getItems().clear();
            allPublicationsListView.getItems().addAll(genericHibernate.getAllRecords(Publication.class));
            applyFilters();
        }
    }

    private void updateFilterVisibility() {
        if (bookFiltersBox != null && mangaFiltersBox != null) {
            boolean isBookSelected = filterBookRadio.isSelected();
            bookFiltersBox.setVisible(isBookSelected);
            bookFiltersBox.setManaged(isBookSelected);
            mangaFiltersBox.setVisible(!isBookSelected);
            mangaFiltersBox.setManaged(!isBookSelected);
            applyFilters();
        }
    }

    private void applyFilters() {
        if (allPublicationsListView == null || genericHibernate == null) return;

        List<Publication> filteredPublications = genericHibernate.getAllRecords(Publication.class).stream()
            .filter(pub -> {
                if (filterBookRadio != null && filterMangaRadio != null) {
                    return filterBookRadio.isSelected() ? pub instanceof Book : pub instanceof Manga;
                }
                return true;
            })
            .filter(pub -> statusFilter.getValue() == null || pub.getStatus() == statusFilter.getValue())
            .filter(pub -> languageFilter.getValue() == null || pub.getLanguage() == languageFilter.getValue())
            .filter(pub -> {
                if (filterBookRadio != null && filterBookRadio.isSelected() && genreFilter.getValue() != null) {
                    return pub instanceof Book && ((Book) pub).getGenre() == genreFilter.getValue();
                }
                return true;
            })
            .filter(pub -> {
                if (filterMangaRadio != null && filterMangaRadio.isSelected() && demographicFilter.getValue() != null) {
                    return pub instanceof Manga && ((Manga) pub).getDemographic() == demographicFilter.getValue();
                }
                return true;
            })
            .filter(pub -> {
                String search = searchField.getText();
                return search == null || search.isEmpty() || 
                       pub.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                       (pub.getDescription() != null && pub.getDescription().toLowerCase().contains(search.toLowerCase()));
            })
            .collect(Collectors.toList());

        allPublicationsListView.getItems().clear();
        allPublicationsListView.getItems().addAll(filteredPublications);
    }

    public void disableFields() {
        // Disable/enable Book-specific fields
        myPublicationIsbnField.setDisable(!myPublicationBookRadio.isSelected());
        myPublicationAuthorField.setDisable(!myPublicationBookRadio.isSelected());
        myPublicationGenreField.setDisable(!myPublicationBookRadio.isSelected());
        mysPublicationYearField.setDisable(!myPublicationBookRadio.isSelected());

        // Disable/enable Manga-specific fields
        illustratorField.setDisable(!myPublicationMangaRadio.isSelected());
        demographicField.setDisable(!myPublicationMangaRadio.isSelected());
        volumeField.setDisable(!myPublicationMangaRadio.isSelected());
        isMangaColoredField.setDisable(!myPublicationMangaRadio.isSelected());

        // Clear fields when switching types
        if (myPublicationBookRadio.isSelected()) {
            illustratorField.clear();
            volumeField.clear();
            demographicField.setValue(null);
            isMangaColoredField.setSelected(false);
        } else {
            myPublicationIsbnField.clear();
            myPublicationAuthorField.clear();
            myPublicationGenreField.setValue(null);
            mysPublicationYearField.setValue(null);
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
        genericHibernate.delete(Publication.class, publication.getId());
        refreshPublicationList();
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

        // Only allow changing to AVAILABLE if currently BORROWED
        if (newStatus == PublicationStatus.AVAILABLE && publication.getStatus() != PublicationStatus.BORROWED) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Can only mark as available if currently borrowed");
            return;
        }

        // Only allow changing to BORROWED if currently AVAILABLE
        if (newStatus == PublicationStatus.BORROWED && publication.getStatus() != PublicationStatus.AVAILABLE) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Can only mark as borrowed if currently available");
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
        Publication selectedPublication = allPublicationsListView.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication first");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("review-dialog.fxml"));
            Stage stage = new Stage();
            Parent parent = fxmlLoader.load();
            
            ReviewDialog reviewDialog = fxmlLoader.getController();
            reviewDialog.setData(selectedPublication, loggedUser, genericHibernate, 
                () -> displayPublicationDetails(selectedPublication));
            
            Scene scene = new Scene(parent);
            stage.setTitle("Write a Review");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to open review dialog: " + e.getMessage());
        }
    }

    @FXML
    public void requestPublication() {
        Publication selectedPublication = allPublicationsListView.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication first");
            return;
        }

        if (selectedPublication.getStatus() != PublicationStatus.AVAILABLE) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "This publication is not available for borrowing");
            return;
        }

        if (selectedPublication.getOwner() == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "This publication has no owner assigned");
            return;
        }

        if (selectedPublication.getOwner().getId() == loggedUser.getId()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "This is your publication - you cannot borrow it");
            return;
        }

        // Check if there's already a pending request
        EntityManager em = genericHibernate.getEntityManager();
        List<PublicationRequest> existingRequests = em.createQuery(
            "SELECT r FROM PublicationRequest r WHERE r.publication = :pub AND r.requester = :user AND r.status = :status",
            PublicationRequest.class)
            .setParameter("pub", selectedPublication)
            .setParameter("user", loggedUser)
            .setParameter("status", PublicationRequest.RequestStatus.PENDING)
            .getResultList();

        if (!existingRequests.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "You already have a pending request for this publication");
            return;
        }

        try {
            PublicationRequest request = new PublicationRequest(loggedUser, selectedPublication);
            genericHibernate.create(request);
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Request submitted successfully! Waiting for owner's approval.");
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to submit request: " + e.getMessage());
        }
    }

    @FXML
    public void viewRequests() {
        Publication selectedPublication = myPublicationListField.getSelectionModel().getSelectedItem();
        if (selectedPublication == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a publication first");
            return;
        }

        EntityManager em = genericHibernate.getEntityManager();
        List<PublicationRequest> requests = em.createQuery(
            "SELECT r FROM PublicationRequest r WHERE r.publication = :pub AND r.status = :status",
            PublicationRequest.class)
            .setParameter("pub", selectedPublication)
            .setParameter("status", PublicationRequest.RequestStatus.PENDING)
            .getResultList();

        if (requests.isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Info", "No pending requests for this publication");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("request-list-dialog.fxml"));
            Stage stage = new Stage();
            Parent parent = fxmlLoader.load();
            
            RequestListDialog dialog = fxmlLoader.getController();
            dialog.setData(requests, genericHibernate, () -> {
                refreshPublicationList();
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Requests processed successfully!");
            });
            
            Scene scene = new Scene(parent);
            stage.setTitle("Publication Requests");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to open requests dialog: " + e.getMessage());
        }
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
        details.append("Owner: ").append(publication.getOwner() != null ? publication.getOwner().getLogin() : "No owner").append("\n");
        details.append("Date Created: ").append(publication.getDateCreated().toString()).append("\n\n");

        // Show pending request status if user has requested this publication and it has an owner
        if (publication.getOwner() != null && loggedUser.getId() != publication.getOwner().getId()) {
            EntityManager em = genericHibernate.getEntityManager();
            List<PublicationRequest> userRequests = em.createQuery(
                "SELECT r FROM PublicationRequest r WHERE r.publication = :pub AND r.requester = :user AND r.status = :status",
                PublicationRequest.class)
                .setParameter("pub", publication)
                .setParameter("user", loggedUser)
                .setParameter("status", PublicationRequest.RequestStatus.PENDING)
                .getResultList();

            if (!userRequests.isEmpty()) {
                details.append("Your request status: PENDING\n\n");
            }
        }

        selectedPublicationDetails.setText(details.toString());
        
        // Refresh comments
        if (commentsTreeView != null) {
            commentsTreeView.setRoot(null);
            if (publication.getCommentList() != null && !publication.getCommentList().isEmpty()) {
                TreeItem<String> root = new TreeItem<>("Comments");
                for (Comment comment : publication.getCommentList()) {
                    if (comment.getParentComment() == null) {
                        root.getChildren().add(createCommentNode(comment));
                    }
                }
                commentsTreeView.setRoot(root);
                commentsTreeView.setShowRoot(false);
            }
        }
    }

    private TreeItem<String> createCommentNode(Comment comment) {
        String commentText;
        String userLogin = comment.getUser() != null ? comment.getUser().getLogin() : "Unknown User";
        String dateStr = comment.getCreatedAt() != null ? comment.getCreatedAt().toLocalDate().toString() : "No date";
        
        if (comment instanceof Rating rating) {
            commentText = String.format("%s - %s (%d★)\n%s", 
                userLogin,
                dateStr,
                rating.getStars(),
                comment.getText()
            );
        } else {
            commentText = String.format("%s - %s\n%s", 
                userLogin,
                dateStr,
                comment.getText()
            );
        }
        TreeItem<String> node = new TreeItem<>(commentText);
        
        if (comment.getReplies() != null) {
            for (Comment reply : comment.getReplies()) {
                node.getChildren().add(createCommentNode(reply));
            }
        }
        
        return node;
    }

    @FXML
    public void openUserManagement() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-management.fxml"));
            Stage stage = new Stage();
            Parent parent = fxmlLoader.load();
            
            UserManagementForm userManagementForm = fxmlLoader.getController();
            userManagementForm.setData(entityManagerFactory, loggedUser);
            
            Scene scene = new Scene(parent);
            stage.setTitle("User Management");
            stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        } catch (IOException e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to open user management: " + e.getMessage());
        }
    }

    private void refreshUserList() {
        if (userTable != null) {
            userTable.getItems().clear();
            if (isAdmin) {
                // Admin sees all users
                List<User> users = genericHibernate.getAllRecords(User.class);
                userTable.getItems().addAll(users);
            } else {
                // Regular users only see themselves
                userTable.getItems().add(loggedUser);
            }
            userTable.refresh();
        }
    }

    private void displayUserDetails(User user) {
        if (loginField == null) return;
        
        loginField.setText(user.getLogin());
        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        emailField.setText(user.getEmail());
        phoneField.setText(user instanceof Admin ? ((Admin) user).getPhoneNum() : "");
        roleComboBox.setValue(user instanceof Admin ? "Admin" : "Client");
        passwordField.clear();
    }

    private void updateUserDetails(User user) {
        user.setLogin(loginField.getText());
        user.setName(nameField.getText());
        user.setSurname(surnameField.getText());
        user.setEmail(emailField.getText());

        if (!passwordField.getText().isEmpty()) {
            user.setPassword(DatabaseUtils.hashPassword(passwordField.getText()));
        }

        if (user instanceof Admin) {
            ((Admin) user).setPhoneNum(phoneField.getText());
        }

        genericHibernate.update(user);
    }

    private void clearUserFields() {
        if (loginField == null) return;
        
        loginField.clear();
        nameField.clear();
        surnameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        roleComboBox.setValue("Client");
    }

    private Comment findCommentByTreeItem(TreeItem<String> item) {
        Publication publication = allPublicationsListView.getSelectionModel().getSelectedItem();
        if (publication == null || item == null) return null;

        String commentText = item.getValue();
        return findCommentInPublication(publication, commentText);
    }

    private Comment findCommentInPublication(Publication publication, String commentText) {
        if (publication.getCommentList() == null) return null;
        
        for (Comment comment : publication.getCommentList()) {
            if (formatCommentText(comment).equals(commentText)) return comment;
            Comment found = findInReplies(comment, commentText);
            if (found != null) return found;
        }
        return null;
    }

    private Comment findInReplies(Comment parent, String commentText) {
        if (parent.getReplies() == null) return null;
        
        for (Comment reply : parent.getReplies()) {
            if (formatCommentText(reply).equals(commentText)) return reply;
            Comment found = findInReplies(reply, commentText);
            if (found != null) return found;
        }
        return null;
    }

    private String formatCommentText(Comment comment) {
        String userLogin = comment.getUser() != null ? comment.getUser().getLogin() : "Unknown User";
        String dateStr = comment.getCreatedAt() != null ? comment.getCreatedAt().toLocalDate().toString() : "No date";
        
        return comment instanceof Rating rating ?
            String.format("%s - %s (%d★)\n%s", userLogin, dateStr, rating.getStars(), comment.getText()) :
            String.format("%s - %s\n%s", userLogin, dateStr, comment.getText());
    }

    private void updateCommentButtons() {
        boolean hasSelectedComment = selectedComment != null;
        boolean isOwner = hasSelectedComment && selectedComment.getUser().getId() == loggedUser.getId();
        
        editCommentButton.setVisible(hasSelectedComment && isOwner);
        deleteCommentButton.setVisible(hasSelectedComment && (isOwner || isAdmin));
        editCommentText.setVisible(false);
    }

    @FXML
    public void handleEditComment() {
        if (selectedComment == null) return;
        
        editCommentText.setVisible(true);
        editCommentText.setText(selectedComment.getText());
    }

    @FXML
    public void handleSaveEdit() {
        if (selectedComment == null || editCommentText.getText().trim().isEmpty()) return;
        
        try {
            selectedComment.edit(editCommentText.getText().trim());
            genericHibernate.update(selectedComment);
            refreshCommentView();
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Comment updated successfully!");
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to update comment: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteComment() {
        if (selectedComment == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Are you sure you want to delete this comment?\nThis action cannot be undone.", 
            ButtonType.YES, ButtonType.NO);
            
        if (alert.showAndWait().get() == ButtonType.YES) {
            try {
                selectedComment.delete();
                genericHibernate.update(selectedComment);
                refreshCommentView();
                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "Comment deleted successfully!");
            } catch (Exception e) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to delete comment: " + e.getMessage());
            }
        }
    }

    private void refreshCommentView() {
        editCommentText.setVisible(false);
        Publication currentPublication = allPublicationsListView.getSelectionModel().getSelectedItem();
        if (currentPublication != null) {
            // Refresh the publication from database to get updated comments
            EntityManager em = genericHibernate.getEntityManager();
            em.refresh(currentPublication);
            
            // Update the publication in the list view
            int index = allPublicationsListView.getItems().indexOf(currentPublication);
            if (index >= 0) {
                allPublicationsListView.getItems().set(index, currentPublication);
            }
            
            // Display updated details
            displayPublicationDetails(currentPublication);
        }
    }

    @FXML
    public void handleNew() {
        clearUserFields();
        loginField.setDisable(false);
    }

    @FXML
    public void handleSave() {
        if (nameField.getText().isEmpty() || 
            surnameField.getText().isEmpty() || 
            emailField.getText().isEmpty()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please fill in all required fields");
            return;
        }

        try {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to update");
                return;
            }

            // Only admins can change roles
            if (isAdmin) {
                if (roleComboBox.getValue().equals("Admin") && !(selectedUser instanceof Admin)) {
                    Admin admin = new Admin();
                    admin.setId(selectedUser.getId());
                    admin.setDateCreated(selectedUser.getDateCreated());
                    selectedUser = admin;
                } else if (roleComboBox.getValue().equals("Client") && !(selectedUser instanceof Client)) {
                    Client client = new Client();
                    client.setId(selectedUser.getId());
                    client.setDateCreated(selectedUser.getDateCreated());
                    selectedUser = client;
                }
            }

            // Set common fields
            selectedUser.setName(nameField.getText());
            selectedUser.setSurname(surnameField.getText());
            selectedUser.setEmail(emailField.getText());

            if (!passwordField.getText().isEmpty()) {
                selectedUser.setPassword(DatabaseUtils.hashPassword(passwordField.getText()));
            }

            // Handle admin-specific fields
            if (selectedUser instanceof Admin) {
                ((Admin) selectedUser).setPhoneNum(phoneField.getText());
            }

            genericHibernate.update(selectedUser);
            
            // If the logged-in user was updated, refresh their info
            if (selectedUser.getId() == loggedUser.getId()) {
                loggedUser = selectedUser;
            }
            
            refreshUserList();
            FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully!");
        } catch (Exception e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to update user: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Please select a user to delete");
            return;
        }

        if (selectedUser.getId() == loggedUser.getId()) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "You cannot delete your own account");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this user?\nThis action cannot be undone.",
            ButtonType.YES, ButtonType.NO);

        if (alert.showAndWait().get() == ButtonType.YES) {
            try {
                // Get all publications owned by the user
                List<Publication> publications = genericHibernate.getEntityManager()
                    .createQuery("SELECT p FROM Publication p WHERE p.owner.id = :userId", Publication.class)
                    .setParameter("userId", selectedUser.getId())
                    .getResultList();

                // For each publication
                for (Publication pub : publications) {
                    // Delete all comments on this publication
                    List<Comment> comments = genericHibernate.getEntityManager()
                        .createQuery("SELECT c FROM Comment c WHERE c.publication.id = :pubId", Comment.class)
                        .setParameter("pubId", pub.getId())
                        .getResultList();
                    
                    for (Comment comment : comments) {
                        genericHibernate.delete(Comment.class, comment.getId());
                    }

                    // Delete all requests for this publication
                    List<PublicationRequest> requests = genericHibernate.getEntityManager()
                        .createQuery("SELECT r FROM PublicationRequest r WHERE r.publication.id = :pubId", PublicationRequest.class)
                        .setParameter("pubId", pub.getId())
                        .getResultList();
                    
                    for (PublicationRequest request : requests) {
                        genericHibernate.delete(PublicationRequest.class, request.getId());
                    }

                    // Delete the publication
                    genericHibernate.delete(Publication.class, pub.getId());
                }

                // Delete all comments made by the user
                List<Comment> userComments = genericHibernate.getEntityManager()
                    .createQuery("SELECT c FROM Comment c WHERE c.user.id = :userId", Comment.class)
                    .setParameter("userId", selectedUser.getId())
                    .getResultList();
                
                for (Comment comment : userComments) {
                    genericHibernate.delete(Comment.class, comment.getId());
                }

                // Delete all publication requests made by the user
                List<PublicationRequest> userRequests = genericHibernate.getEntityManager()
                    .createQuery("SELECT r FROM PublicationRequest r WHERE r.requester.id = :userId", PublicationRequest.class)
                    .setParameter("userId", selectedUser.getId())
                    .getResultList();
                
                for (PublicationRequest request : userRequests) {
                    genericHibernate.delete(PublicationRequest.class, request.getId());
                }

                // Finally delete the user
                genericHibernate.delete(User.class, selectedUser.getId());

                // Update UI
                userTable.getItems().remove(selectedUser);
                clearUserFields();
                refreshUserList();

                FxUtils.generateAlert(Alert.AlertType.INFORMATION, "Success", "User and all associated data deleted successfully!");
            } catch (Exception e) {
                FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void createNewUser() {
        if (!isAdmin) {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Only administrators can create new users");
            return;
        }
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration-form.fxml"));
            Stage stage = new Stage();
            Parent parent = fxmlLoader.load();
            
            RegistrationForm registrationForm = fxmlLoader.getController();
            registrationForm.setData(entityManagerFactory);
            registrationForm.setData(entityManagerFactory);
            registrationForm.setAdminMode(true);
            
            Scene scene = new Scene(parent);
            stage.setTitle("Create New User");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh the user list after creation
            refreshUserList();
        } catch (IOException e) {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to open user creation form: " + e.getMessage());
        }
    }
}
