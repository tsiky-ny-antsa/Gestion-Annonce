package com.ffr.controllers;

import com.ffr.MainApp;
import com.ffr.dao.*;
import com.ffr.models.*;
import com.ffr.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MainController {

    private TabPane tabPane;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private AnnonceDAO annonceDAO;
    private ProgrammeDAO programmeDAO;

    private TableView<Annonce> annonceTable;
    private TableView<Programme> programmeTable;
    private TableView<Category> categoryTable;
    private TableView<User> userTable;

    public MainController() {
        this.userDAO = new UserDAO();
        this.categoryDAO = new CategoryDAO();
        this.annonceDAO = new AnnonceDAO();
        this.programmeDAO = new ProgrammeDAO();
    }

    public Parent createMainView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topBar = createTopBar();
        root.setTop(topBar);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab annoncesTab = new Tab("Annonces", createAnnoncesView());
        Tab programmesTab = new Tab("Programmes", createProgrammesView());
        Tab onAirTab = new Tab("On-Air", createOnAirView());

        tabPane.getTabs().addAll(annoncesTab, programmesTab, onAirTab);

        if (SessionManager.getInstance().isRoot()) {
            Tab categoriesTab = new Tab("Catégories", createCategoriesView());
            Tab gestionTab = new Tab("Gestion", createGestionView());
            tabPane.getTabs().addAll(categoriesTab, gestionTab);
        }

        root.setCenter(tabPane);

        return root;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #4a8c2a;");

        Label titleLabel = new Label("FFR Stage - Gestion des Annonces");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        Label userLabel = new Label("Utilisateur: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setStyle("-fx-text-fill: white;");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> handleLogout());

        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);

        return topBar;
    }

    private Parent createAnnoncesView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        annonceTable = new TableView<>();
        
        TableColumn<Annonce, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Annonce, String> titleCol = new TableColumn<>("Titre");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Annonce, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryCol.setPrefWidth(150);

        TableColumn<Annonce, Integer> nbrPrevCol = new TableColumn<>("Nbr Prev");
        nbrPrevCol.setCellValueFactory(new PropertyValueFactory<>("nbrPrev"));
        nbrPrevCol.setPrefWidth(80);

        TableColumn<Annonce, Integer> nbrDifCol = new TableColumn<>("Nbr Dif");
        nbrDifCol.setCellValueFactory(new PropertyValueFactory<>("nbrDif"));
        nbrDifCol.setPrefWidth(80);

        TableColumn<Annonce, String> createdByCol = new TableColumn<>("Créé par");
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdByUsername"));
        createdByCol.setPrefWidth(120);

        annonceTable.getColumns().addAll(idCol, titleCol, categoryCol, nbrPrevCol, nbrDifCol, createdByCol);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        buttonBar.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Nouvelle Annonce");
        addButton.setOnAction(e -> handleAddAnnonce());

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> handleEditAnnonce());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> handleDeleteAnnonce());

        Button viewProgrammesButton = new Button("Voir Programmes");
        viewProgrammesButton.setOnAction(e -> handleViewAnnonceProgrammes());

        buttonBar.getChildren().addAll(addButton, editButton, deleteButton, viewProgrammesButton);

        layout.setTop(buttonBar);
        layout.setCenter(annonceTable);

        refreshAnnonces();

        return layout;
    }

    private Parent createProgrammesView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        VBox topSection = new VBox(10);
        
        HBox dateSelector = new HBox(10);
        dateSelector.setAlignment(Pos.CENTER_LEFT);
        
        Label dateLabel = new Label("Sélectionner une date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        Button loadButton = new Button("Charger");
        loadButton.setOnAction(e -> loadProgrammesForDate(datePicker.getValue()));

        dateSelector.getChildren().addAll(dateLabel, datePicker, loadButton);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        Button addButton = new Button("Ajouter Programme");
        addButton.setOnAction(e -> handleAddProgramme());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> handleDeleteProgramme());

        buttonBar.getChildren().addAll(addButton, deleteButton);

        topSection.getChildren().addAll(dateSelector, buttonBar);

        programmeTable = new TableView<>();
        
        TableColumn<Programme, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Programme, String> annonceCol = new TableColumn<>("Annonce");
        annonceCol.setCellValueFactory(new PropertyValueFactory<>("annonceTitle"));
        annonceCol.setPrefWidth(250);

        TableColumn<Programme, LocalDateTime> dateCol = new TableColumn<>("Date/Heure");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("datePro"));
        dateCol.setPrefWidth(180);

        TableColumn<Programme, Boolean> dif1Col = new TableColumn<>("Dif1");
        dif1Col.setCellValueFactory(new PropertyValueFactory<>("dif1"));
        dif1Col.setPrefWidth(60);

        TableColumn<Programme, Boolean> dif2Col = new TableColumn<>("Dif2");
        dif2Col.setCellValueFactory(new PropertyValueFactory<>("dif2"));
        dif2Col.setPrefWidth(60);

        TableColumn<Programme, Boolean> dif3Col = new TableColumn<>("Dif3");
        dif3Col.setCellValueFactory(new PropertyValueFactory<>("dif3"));
        dif3Col.setPrefWidth(60);

        programmeTable.getColumns().addAll(idCol, annonceCol, dateCol, dif1Col, dif2Col, dif3Col);

        layout.setTop(topSection);
        layout.setCenter(programmeTable);

        loadProgrammesForDate(LocalDate.now());

        return layout;
    }

    private Parent createOnAirView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        Label titleLabel = new Label("Programme d'aujourd'hui");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        VBox dif1Box = createDiffusionBox("Diffusion 1", 1);
        VBox dif2Box = createDiffusionBox("Diffusion 2", 2);
        VBox dif3Box = createDiffusionBox("Diffusion 3", 3);

        content.getChildren().addAll(dif1Box, dif2Box, dif3Box);

        Button refreshButton = new Button("Actualiser");
        refreshButton.setOnAction(e -> refreshOnAir(dif1Box, dif2Box, dif3Box));

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(titleLabel, refreshButton);

        layout.setTop(topBar);
        layout.setCenter(new ScrollPane(content));

        refreshOnAir(dif1Box, dif2Box, dif3Box);

        return layout;
    }

    private VBox createDiffusionBox(String title, int difNumber) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #a8d88b; -fx-border-width: 2; -fx-background-color: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a8c2a;");

        box.getChildren().add(titleLabel);
        box.setUserData(difNumber);

        return box;
    }

    private void refreshOnAir(VBox dif1Box, VBox dif2Box, VBox dif3Box) {
        try {
            List<Programme> todayProgrammes = programmeDAO.getTodayProgrammes();

            dif1Box.getChildren().clear();
            dif2Box.getChildren().clear();
            dif3Box.getChildren().clear();

            Label titleLabel1 = new Label("Diffusion 1");
            titleLabel1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a8c2a;");
            dif1Box.getChildren().add(titleLabel1);

            Label titleLabel2 = new Label("Diffusion 2");
            titleLabel2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a8c2a;");
            dif2Box.getChildren().add(titleLabel2);

            Label titleLabel3 = new Label("Diffusion 3");
            titleLabel3.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4a8c2a;");
            dif3Box.getChildren().add(titleLabel3);

            for (Programme prog : todayProgrammes) {
                if (prog.isDif1()) {
                    dif1Box.getChildren().add(new Label("• " + prog.getAnnonceTitle()));
                }
                if (prog.isDif2()) {
                    dif2Box.getChildren().add(new Label("• " + prog.getAnnonceTitle()));
                }
                if (prog.isDif3()) {
                    dif3Box.getChildren().add(new Label("• " + prog.getAnnonceTitle()));
                }
            }

            if (dif1Box.getChildren().size() == 1) dif1Box.getChildren().add(new Label("Aucune annonce"));
            if (dif2Box.getChildren().size() == 1) dif2Box.getChildren().add(new Label("Aucune annonce"));
            if (dif3Box.getChildren().size() == 1) dif3Box.getChildren().add(new Label("Aucune annonce"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parent createCategoriesView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        categoryTable = new TableView<>();
        
        TableColumn<Category, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);

        TableColumn<Category, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Category, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(400);

        categoryTable.getColumns().addAll(idCol, nameCol, descCol);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        buttonBar.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Nouvelle Catégorie");
        addButton.setOnAction(e -> handleAddCategory());

        Button editButton = new Button("Modifier");
        editButton.setOnAction(e -> handleEditCategory());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> handleDeleteCategory());

        buttonBar.getChildren().addAll(addButton, editButton, deleteButton);

        layout.setTop(buttonBar);
        layout.setCenter(categoryTable);

        refreshCategories();

        return layout;
    }

    private Parent createGestionView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(15));

        userTable = new TableView<>();
        
        TableColumn<User, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);

        TableColumn<User, String> usernameCol = new TableColumn<>("Nom d'utilisateur");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(200);

        TableColumn<User, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(150);

        TableColumn<User, LocalDateTime> createdCol = new TableColumn<>("Créé le");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setPrefWidth(200);

        userTable.getColumns().addAll(idCol, usernameCol, roleCol, createdCol);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_LEFT);
        buttonBar.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Nouvel Utilisateur");
        addButton.setOnAction(e -> handleAddUser());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setOnAction(e -> handleDeleteUser());

        buttonBar.getChildren().addAll(addButton, deleteButton);

        layout.setTop(buttonBar);
        layout.setCenter(userTable);

        refreshUsers();

        return layout;
    }

    private void handleAddAnnonce() {
        Dialog<Annonce> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Annonce");
        dialog.setHeaderText("Créer une nouvelle annonce");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Titre");
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Contenu");
        contentArea.setPrefRowCount(3);
        ComboBox<Category> categoryCombo = new ComboBox<>();
        TextField propField = new TextField();
        propField.setPromptText("Propriétaire");
        TextField typeField = new TextField();
        typeField.setPromptText("Type");
        TextField nbrPrevField = new TextField("0");
        nbrPrevField.setPromptText("Nombre de diffusions prévues");
        Label audioLabel = new Label("Aucun fichier");
        Button audioButton = new Button("Choisir Audio");
        
        final String[] audioPath = {null};
        audioButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un fichier audio");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Audio", "*.mp3", "*.wav", "*.ogg"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
            );
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                try {
                    File audioDir = new File("src/main/resources/audio");
                    if (!audioDir.exists()) audioDir.mkdirs();
                    
                    File destFile = new File(audioDir, file.getName());
                    Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    audioPath[0] = "audio/" + file.getName();
                    audioLabel.setText(file.getName());
                } catch (Exception ex) {
                    showAlert("Erreur", "Erreur lors de la copie du fichier audio: " + ex.getMessage());
                }
            }
        });

        try {
            categoryCombo.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Contenu:"), 0, 1);
        grid.add(contentArea, 1, 1);
        grid.add(new Label("Catégorie:"), 0, 2);
        grid.add(categoryCombo, 1, 2);
        grid.add(new Label("Propriétaire:"), 0, 3);
        grid.add(propField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeField, 1, 4);
        grid.add(new Label("Nbr Prev:"), 0, 5);
        grid.add(nbrPrevField, 1, 5);
        grid.add(new Label("Fichier Audio:"), 0, 6);
        grid.add(audioButton, 1, 6);
        grid.add(audioLabel, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Annonce annonce = new Annonce();
                annonce.setTitle(titleField.getText());
                annonce.setContent(contentArea.getText());
                if (categoryCombo.getValue() != null) {
                    annonce.setCategoryId(categoryCombo.getValue().getId());
                }
                annonce.setProp(propField.getText());
                annonce.setType(typeField.getText());
                try {
                    annonce.setNbrPrev(Integer.parseInt(nbrPrevField.getText()));
                } catch (NumberFormatException e) {
                    annonce.setNbrPrev(0);
                }
                annonce.setAudioPath(audioPath[0]);
                annonce.setCreatedBy(SessionManager.getInstance().getCurrentUser().getId());
                return annonce;
            }
            return null;
        });

        Optional<Annonce> result = dialog.showAndWait();
        result.ifPresent(annonce -> {
            try {
                Long annonceId = annonceDAO.createAnnonce(annonce);
                if (annonceId != null) {
                    showAlert("Succès", "Annonce créée avec succès!");
                    refreshAnnonces();
                    
                    Alert progAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    progAlert.setTitle("Programmer l'annonce");
                    progAlert.setHeaderText("Voulez-vous programmer cette annonce maintenant?");
                    progAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            annonce.setId(annonceId);
                            handleAddProgrammeForAnnonce(annonce);
                        }
                    });
                }
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleEditAnnonce() {
        Annonce selected = annonceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une annonce");
            return;
        }

        showAlert("Information", "Fonction de modification à implémenter");
    }

    private void handleDeleteAnnonce() {
        Annonce selected = annonceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une annonce");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'annonce?");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette annonce?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    annonceDAO.deleteAnnonce(selected.getId());
                    refreshAnnonces();
                    showAlert("Succès", "Annonce supprimée");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void handleViewAnnonceProgrammes() {
        Annonce selected = annonceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une annonce");
            return;
        }

        try {
            List<Programme> programmes = programmeDAO.getProgrammesByAnnonce(selected.getId());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Programmes de l'annonce");
            alert.setHeaderText("Programmes pour: " + selected.getTitle());
            
            StringBuilder content = new StringBuilder();
            if (programmes.isEmpty()) {
                content.append("Aucun programme trouvé");
            } else {
                for (Programme p : programmes) {
                    content.append("Date: ").append(p.getDatePro())
                           .append(" - Dif1:").append(p.isDif1())
                           .append(" Dif2:").append(p.isDif2())
                           .append(" Dif3:").append(p.isDif3())
                           .append("\n");
                }
            }
            alert.setContentText(content.toString());
            alert.showAndWait();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur: " + e.getMessage());
        }
    }

    private void handleAddProgramme() {
        Dialog<Programme> dialog = createProgrammeDialog(null);
        Optional<Programme> result = dialog.showAndWait();
        
        result.ifPresent(programme -> {
            try {
                programmeDAO.createProgramme(programme);
                showAlert("Succès", "Programme créé avec succès!");
                loadProgrammesForDate(programme.getDatePro().toLocalDate());
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
            }
        });
    }

    private void handleAddProgrammeForAnnonce(Annonce annonce) {
        Dialog<Programme> dialog = createProgrammeDialog(annonce);
        Optional<Programme> result = dialog.showAndWait();
        
        result.ifPresent(programme -> {
            try {
                programmeDAO.createProgramme(programme);
                showAlert("Succès", "Programme créé avec succès!");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
            }
        });
    }

    private Dialog<Programme> createProgrammeDialog(Annonce preselectedAnnonce) {
        Dialog<Programme> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Programme");
        dialog.setHeaderText("Créer un nouveau programme");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Annonce> annonceCombo = new ComboBox<>();
        DatePicker datePicker = new DatePicker(LocalDate.now());
        CheckBox dif1Check = new CheckBox("Diffusion 1");
        CheckBox dif2Check = new CheckBox("Diffusion 2");
        CheckBox dif3Check = new CheckBox("Diffusion 3");

        try {
            List<Annonce> annonces = annonceDAO.getAllAnnonces();
            annonceCombo.setItems(FXCollections.observableArrayList(annonces));
            if (preselectedAnnonce != null) {
                annonceCombo.setValue(preselectedAnnonce);
                annonceCombo.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        grid.add(new Label("Annonce:"), 0, 0);
        grid.add(annonceCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Créneaux:"), 0, 2);
        grid.add(dif1Check, 1, 2);
        grid.add(dif2Check, 1, 3);
        grid.add(dif3Check, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Programme programme = new Programme();
                if (annonceCombo.getValue() != null) {
                    programme.setAnnonceId(annonceCombo.getValue().getId());
                }
                programme.setDatePro(datePicker.getValue().atStartOfDay());
                programme.setDif1(dif1Check.isSelected());
                programme.setDif2(dif2Check.isSelected());
                programme.setDif3(dif3Check.isSelected());
                return programme;
            }
            return null;
        });

        return dialog;
    }

    private void handleDeleteProgramme() {
        Programme selected = programmeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un programme");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le programme?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    programmeDAO.deleteProgramme(selected.getId());
                    refreshProgrammes();
                    showAlert("Succès", "Programme supprimé");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur: " + e.getMessage());
                }
            }
        });
    }

    private void loadProgrammesForDate(LocalDate date) {
        try {
            List<Programme> programmes = programmeDAO.getProgrammesByDate(date);
            programmeTable.setItems(FXCollections.observableArrayList(programmes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAddCategory() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Catégorie");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");
        TextField descField = new TextField();
        descField.setPromptText("Description");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Category category = new Category();
                category.setName(nameField.getText());
                category.setDescription(descField.getText());
                return category;
            }
            return null;
        });

        Optional<Category> result = dialog.showAndWait();
        result.ifPresent(category -> {
            try {
                categoryDAO.createCategory(category.getName(), category.getDescription());
                refreshCategories();
                showAlert("Succès", "Catégorie créée");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur: " + e.getMessage());
            }
        });
    }

    private void handleEditCategory() {
        showAlert("Information", "Fonction à implémenter");
    }

    private void handleDeleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une catégorie");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la catégorie?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categoryDAO.deleteCategory(selected.getId());
                    refreshCategories();
                    showAlert("Succès", "Catégorie supprimée");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur: " + e.getMessage());
                }
            }
        });
    }

    private void handleAddUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Nouvel Utilisateur");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("user", "root");
        roleCombo.setValue("user");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Rôle:"), 0, 2);
        grid.add(roleCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setPassword(passwordField.getText());
                user.setRole(roleCombo.getValue());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            try {
                userDAO.createUser(user.getUsername(), user.getPassword(), user.getRole());
                refreshUsers();
                showAlert("Succès", "Utilisateur créé");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur: " + e.getMessage());
            }
        });
    }

    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un utilisateur");
            return;
        }

        if (selected.getId().equals(SessionManager.getInstance().getCurrentUser().getId())) {
            showAlert("Erreur", "Vous ne pouvez pas supprimer votre propre compte");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'utilisateur?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userDAO.deleteUser(selected.getId());
                    refreshUsers();
                    showAlert("Succès", "Utilisateur supprimé");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur: " + e.getMessage());
                }
            }
        });
    }

    private void handleLogout() {
        try {
            SessionManager.getInstance().logout();
            MainApp.showLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshAnnonces() {
        try {
            List<Annonce> annonces = annonceDAO.getAllAnnonces();
            annonceTable.setItems(FXCollections.observableArrayList(annonces));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshProgrammes() {
        try {
            List<Programme> programmes = programmeDAO.getAllProgrammes();
            programmeTable.setItems(FXCollections.observableArrayList(programmes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            categoryTable.setItems(FXCollections.observableArrayList(categories));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
