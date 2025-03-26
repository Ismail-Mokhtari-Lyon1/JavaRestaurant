package com.example.zozo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

        // Colonne 4 : Stock final (fond vert clair)
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

        // Bouton Retour
        Button backButton = new Button("Retour");
        backButton.getStyleClass().add("dashboard-button");
        backButton.setOnAction(e -> onBack.run());
        HBox buttonBox = new HBox(backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20));

        VBox vbox = new VBox(20, table, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 800, 600);
        scene.getStylesheets().add(Stocks.class.getResource("statistiques.css").toExternalForm());
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
