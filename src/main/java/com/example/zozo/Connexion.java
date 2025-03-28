package com.example.zozo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Connexion {

    public static Scene getLoginScene(Stage primaryStage, Consumer<String> onSuccess) {
        // --- Header ---
        Label headerLabel = new Label("Connexion");
        headerLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox header = new HBox(headerLabel);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #007ACC;");

        // --- Formulaire de connexion ---
        Label userLabel = new Label("Nom d'utilisateur :");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Mot de passe :");
        PasswordField passwordField = new PasswordField();
        Label messageLabel = new Label();
        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-background-color: #007ACC; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.equals("serveur") && password.equals("serveur123")) {
                onSuccess.accept("serveur");
            } else if (username.equals("gerant") && password.equals("gerant123")) {
                onSuccess.accept("gerant");
            } else {
                messageLabel.setText("Identifiants incorrects !");
            }
        });

        VBox form = new VBox(10, userLabel, usernameField, passLabel, passwordField, loginButton, messageLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));

        // --- Assemblage global ---
        VBox mainPane = new VBox(header, form);
        mainPane.setAlignment(Pos.TOP_CENTER);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(10));

        return new Scene(mainPane, 400, 300);
    }
}
