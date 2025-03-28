package com.example.zozo;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class Stocks {

    public static Scene getStocksScene(Stage primaryStage, Runnable onBack) {
        // Extraction des ingrédients uniques à partir des menus de RestaurantApp
        Set<String> ingredientsSet = new HashSet<>();
        for (Menu menu : RestaurantApp.menus) {
            // On suppose que menu.getItems() retourne une chaîne avec les ingrédients séparés par des virgules.
            String[] parts = menu.getItems().split(",");
            for (String part : parts) {
                ingredientsSet.add(part.trim());
            }
        }
        List<String> ingredients = new ArrayList<>(ingredientsSet);
        Collections.sort(ingredients);

        // Calcul des ingrédients consommés aujourd'hui à partir des commandes
        Map<String, Integer> consumedMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (Commande cmd : RestaurantApp.commandes) {
            if (cmd.getDate() != null && cmd.getDate().equals(today)) {
                for (Menu menu : cmd.getItems()) {
                    String[] parts = menu.getItems().split(",");
                    for (String part : parts) {
                        String ingr = part.trim();
                        consumedMap.put(ingr, consumedMap.getOrDefault(ingr, 0) + 1);
                    }
                }
            }
        }

        // Générer des stocks initiaux aléatoires pour chaque ingrédient (entre 50 et 200)
        Map<String, Integer> initialStockMap = new HashMap<>();
        Random rand = new Random();
        for (String ingr : ingredients) {
            int stockInitial = 50 + rand.nextInt(151);
            initialStockMap.put(ingr, stockInitial);
        }

        // Calcul des stocks finaux : stock initial - consommé
        Map<String, Integer> finalStockMap = new HashMap<>();
        for (String ingr : ingredients) {
            int consumed = consumedMap.getOrDefault(ingr, 0);
            int initial = initialStockMap.getOrDefault(ingr, 0);
            finalStockMap.put(ingr, initial - consumed);
        }

        // Création du modèle pour la TableView
        ObservableList<StockItem> data = FXCollections.observableArrayList();
        for (String ingr : ingredients) {
            int consumed = consumedMap.getOrDefault(ingr, 0);
            int initial = initialStockMap.get(ingr);
            int finalStock = finalStockMap.get(ingr);
            data.add(new StockItem(ingr, consumed, initial, finalStock));
        }

        // Création du TableView
        TableView<StockItem> table = new TableView<>();
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Colonne 1 : Ingrédient
        TableColumn<StockItem, String> colIngredient = new TableColumn<>("Ingrédient");
        colIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredient"));
        colIngredient.setMinWidth(200);

        // Colonne 2 : Consommé
        TableColumn<StockItem, Integer> colConsumed = new TableColumn<>("Consommé");
        colConsumed.setCellValueFactory(new PropertyValueFactory<>("consumed"));
        colConsumed.setMinWidth(100);

        // Colonne 3 : Stock initial
        TableColumn<StockItem, Integer> colInitial = new TableColumn<>("Stock initial");
        colInitial.setCellValueFactory(new PropertyValueFactory<>("initialStock"));
        colInitial.setMinWidth(100);

        // Colonne 4 : Stock final (avec fond vert clair)
        TableColumn<StockItem, Integer> colFinal = new TableColumn<>("Stock final");
        colFinal.setCellValueFactory(new PropertyValueFactory<>("finalStock"));
        colFinal.setMinWidth(100);
        colFinal.setCellFactory(column -> new TableCell<StockItem, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color: lightgreen;");
                }
            }
        });

        table.getColumns().addAll(colIngredient, colConsumed, colInitial, colFinal);

        // Ajuster dynamiquement la hauteur du tableau en fonction du nombre de lignes
        double fixedCellSize = 30;
        table.setFixedCellSize(fixedCellSize);
        table.prefHeightProperty().bind(
                Bindings.size(table.getItems()).multiply(fixedCellSize).add(28)
        );

        // Bouton Retour
        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #007ACC; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        backButton.setOnAction(e -> onBack.run());
        HBox buttonBox = new HBox(backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        // Création du header pour la page Stocks
        Label headerTitle = new Label("Gestion des Stocks");
        headerTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox headerBox = new HBox(headerTitle);
        headerBox.setStyle("-fx-background-color: #007ACC; -fx-padding: 20;");
        headerBox.setAlignment(Pos.CENTER);

        // Organisation globale du layout dans un VBox
        VBox mainContent = new VBox(20, headerBox, table, buttonBox);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-border-radius: 5;");

        // Envelopper mainContent dans un ScrollPane pour le défilement
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        Scene scene = new Scene(scrollPane, 700, 1200);

        return scene;

    }

    // Classe modèle pour représenter un item de stock
    public static class StockItem {
        private final String ingredient;
        private final int consumed;
        private final int initialStock;
        private final int finalStock;

        public StockItem(String ingredient, int consumed, int initialStock, int finalStock) {
            this.ingredient = ingredient;
            this.consumed = consumed;
            this.initialStock = initialStock;
            this.finalStock = finalStock;
        }

        public String getIngredient() {
            return ingredient;
        }

        public int getConsumed() {
            return consumed;
        }

        public int getInitialStock() {
            return initialStock;
        }

        public int getFinalStock() {
            return finalStock;
        }
    }
}
