package com.ffr.controllers;

import com.ffr.MainApp;
import com.ffr.dao.UserDAO;
import com.ffr.models.User;
import com.ffr.utils.SessionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginController {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
    }

    public Parent createLoginView() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20));
        container.getStyleClass().add("login-container");

        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(30));
        loginBox.getStyleClass().add("login-box");
        loginBox.setMaxWidth(300);

        Label titleLabel = new Label("FFR Stage");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("title-label");

        Label subtitleLabel = new Label("Gestion des Annonces");
        subtitleLabel.setStyle("-fx-text-fill: #666;");

        usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setMaxWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(250);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button loginButton = new Button("Se connecter");
        loginButton.setMaxWidth(250);
        loginButton.setOnAction(e -> handleLogin());

        passwordField.setOnAction(e -> handleLogin());

        loginBox.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Label(""),
            usernameField,
            passwordField,
            errorLabel,
            loginButton
        );

        container.getChildren().add(loginBox);

        return container;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                MainApp.showMainScreen();
            } else {
                showError("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (Exception e) {
            showError("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
