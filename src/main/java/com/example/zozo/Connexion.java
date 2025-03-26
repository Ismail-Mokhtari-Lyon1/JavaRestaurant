package com.example.zozo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Connexion {

    public static Scene getLoginScene(Stage primaryStage, Consumer<String> onSuccess) {
        VBox loginPane = new VBox(10);
        loginPane.setPadding(new Insets(20));
        loginPane.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Connexion");
        loginLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label userLabel = new Label("Nom d'utilisateur :");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Mot de passe :");
        PasswordField passwordField = new PasswordField();

        Label messageLabel = new Label();
        Button loginButton = new Button("Se connecter");

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

        loginPane.getChildren().addAll(loginLabel, userLabel, usernameField, passLabel, passwordField, loginButton, messageLabel);
        return new Scene(loginPane, 400, 300);
    }
}
