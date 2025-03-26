package com.example.zozo;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RestaurantApp extends Application {
    public static List<Menu> menus = new ArrayList<>();
    public static List<Commande> commandes = new ArrayList<>();
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Utilisation de la page de connexion
        primaryStage.setScene(Connexion.getLoginScene(primaryStage, role -> {
            showRecapScene(primaryStage, role);
        }));
        primaryStage.setTitle("Connexion");
        primaryStage.show();
    }

    public void showRecapScene(Stage primaryStage, String role) {
        // Initialisation des menus et commandes
        menus.clear();
        commandes.clear();
        menus.add(new Menu("Menu 1", "Coca-Cola, Steak frites, Tiramisu", 20.00));
        menus.add(new Menu("Menu 2", "Coca-Cola, Salade César, Tiramisu", 18.00));
        menus.add(new Menu("Menu 3", "Coca-Cola, Steak frites, Salade César", 22.00));
        menus.add(new Menu("Menu 4", "Coca-Cola, Tiramisu, Salade César", 16.50));

        // Simulation de commandes avec heure et date
        Commande commande1 = new Commande();
        commande1.ajouterMenu(menus.get(0));
        commande1.ajouterMenu(menus.get(2));
        commande1.setHeure(LocalTime.of(12, 30));
        commande1.setDate(LocalDate.now());

        Commande commande2 = new Commande();
        commande2.ajouterMenu(menus.get(1));
        commande2.ajouterMenu(menus.get(0));
        commande2.setHeure(LocalTime.of(20, 15));
        commande2.setDate(LocalDate.now());

        Commande commande3 = new Commande();
        commande3.ajouterMenu(menus.get(2));
        commande3.ajouterMenu(menus.get(3));
        commande3.setHeure(LocalTime.of(13, 15));
        commande3.setDate(LocalDate.now());

        commandes.add(commande1);
        commandes.add(commande2);
        commandes.add(commande3);

        // Affichage en haut à gauche de la date actuelle formatée en français
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", java.util.Locale.FRENCH);
        String formattedDate = currentDate.format(dateFormatter);
        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox topBar = new HBox(dateLabel);
        topBar.setAlignment(Pos.TOP_LEFT);

        // Création d'une TableView pour afficher les commandes
        TableView<CommandeAffichee> commandesTable = new TableView<>();
        ObservableList<CommandeAffichee> commandesData = FXCollections.observableArrayList();

        for (Commande cmd : commandes) {
            String heure = cmd.getHeure().format(timeFormatter);
            double total = cmd.getItems().stream().mapToDouble(Menu::getPrice).sum();
            StringBuilder menusCommande = new StringBuilder();
            for (Menu m : cmd.getItems()) {
                menusCommande.append(m.getName()).append(" | ");
            }
            // Ici, on utilise la classe CommandeAffichee et non Commande
            commandesData.add(new CommandeAffichee(heure, menusCommande.toString(), String.format("%.2f €", total)));
        }

        TableColumn<CommandeAffichee, String> heureCol = new TableColumn<>("Heure");
        heureCol.setCellValueFactory(data -> data.getValue().heureProperty());
        heureCol.setMinWidth(100);

        TableColumn<CommandeAffichee, String> menusCol = new TableColumn<>("Menus commandés");
        menusCol.setCellValueFactory(data -> data.getValue().menusProperty());
        menusCol.setMinWidth(300);

        TableColumn<CommandeAffichee, String> totalCol = new TableColumn<>("Montant total");
        totalCol.setCellValueFactory(data -> data.getValue().totalProperty());
        totalCol.setMinWidth(150);

        commandesTable.getColumns().addAll(heureCol, menusCol, totalCol);
        commandesTable.setItems(commandesData);

        // Zone texte récapitulative affichant le montant total par commande
        TextArea totalCommandesText = new TextArea();
        totalCommandesText.setEditable(false);
        totalCommandesText.setPrefHeight(100);
        StringBuilder recap = new StringBuilder("Montant total par commande :\n");
        int index = 1;
        for (Commande cmd : commandes) {
            double total = cmd.getItems().stream().mapToDouble(Menu::getPrice).sum();
            recap.append("Commande ").append(index++).append(" (")
                    .append(cmd.getHeure().format(timeFormatter))
                    .append(") : ").append(String.format("%.2f €", total)).append("\n");
        }
        totalCommandesText.setText(recap.toString());

        // Boutons en bas : Retour à la connexion (pour tous) et Statistiques (pour le gérant)
        Button retourButton = new Button("Retour à la connexion");
        retourButton.setOnAction(e -> {
            primaryStage.setScene(Connexion.getLoginScene(primaryStage, r -> showRecapScene(primaryStage, r)));
            primaryStage.setTitle("Connexion");
        });
        HBox bottomButtons = new HBox(10, retourButton);
        bottomButtons.setAlignment(Pos.CENTER);

        if (role.equalsIgnoreCase("gerant")) {
            Button statsButton = new Button("Statistiques");
            statsButton.setOnAction(e -> {
                // Accès à la page Statistiques
                primaryStage.setScene(Statistiques.getStatScene(primaryStage, () -> showRecapScene(primaryStage, role)));
                primaryStage.setTitle("Statistiques");
            });
            bottomButtons.getChildren().add(statsButton);
        }

        VBox vbox = new VBox(20, topBar, commandesTable, totalCommandesText, bottomButtons);
        vbox.setPadding(new Insets(15));

        StackPane root = new StackPane(vbox);
        Scene recapScene = new Scene(root, 700, 500);
        primaryStage.setScene(recapScene);
        primaryStage.setTitle("Récapitulatif des Commandes");
    }

    // Classe interne pour représenter une commande dans la TableView
    public static class CommandeAffichee {
        private final SimpleStringProperty heure;
        private final SimpleStringProperty menus;
        private final SimpleStringProperty total;

        public CommandeAffichee(String heure, String menus, String total) {
            this.heure = new SimpleStringProperty(heure);
            this.menus = new SimpleStringProperty(menus);
            this.total = new SimpleStringProperty(total);
        }

        public SimpleStringProperty heureProperty() {
            return heure;
        }

        public SimpleStringProperty menusProperty() {
            return menus;
        }

        public SimpleStringProperty totalProperty() {
            return total;
        }
    }
}
