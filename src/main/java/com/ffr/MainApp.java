package com.ffr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.ffr.controllers.LoginController;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        LoginController controller = new LoginController();
        Scene scene = new Scene(controller.createLoginView(), 400, 300);
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
        
        primaryStage.setTitle("FFR Stage - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showMainScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/fxml/main.fxml"));
        
        Scene scene;
        try {
            Parent root = loader.load();
            scene = new Scene(root, 1200, 700);
        } catch (Exception e) {
            com.ffr.controllers.MainController controller = new com.ffr.controllers.MainController();
            scene = new Scene(controller.createMainView(), 1200, 700);
        }
        
        scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
        
        primaryStage.setTitle("FFR Stage - Gestion des Annonces");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
