package com.restaurant1.demo;

import com.restaurant1.demo.Commande;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;  // Importation du Comparator
import javafx.scene.layout.StackPane;



public class RestaurantApp extends Application {
    private static List<Menu> menus = new ArrayList<>();
    private static List<Commande> commandes = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialiser les menus
        menus.add(new Menu("Menu 1", "Coca-Cola, Steak frites, Tiramisu", 20.00));
        menus.add(new Menu("Menu 2", "Coca-Cola, Salade César, Tiramisu", 18.00));
        menus.add(new Menu("Menu 3", "Coca-Cola, Steak frites, Salade César", 22.00));
        menus.add(new Menu("Menu 4", "Coca-Cola, Tiramisu, Salade César", 16.50));

        // Simuler quelques commandes
        Commande commande1 = new Commande();
        commande1.ajouterMenu(menus.get(0));
        commande1.ajouterMenu(menus.get(2));

        Commande commande2 = new Commande();
        commande2.ajouterMenu(menus.get(1));
        commande2.ajouterMenu(menus.get(0));

        commandes.add(commande1);
        commandes.add(commande2);

        // Statistiques
        Map<String, Integer> menuCount = new HashMap<>();
        for (Commande commande : commandes) {
            for (Menu menu : commande.getItems()) {
                menuCount.put(menu.getName(), menuCount.getOrDefault(menu.getName(), 0) + 1);
            }
        }

        // Liste des menus
        ObservableList<Menu> menuList = FXCollections.observableArrayList(menus);
        ObservableList<String> stats = FXCollections.observableArrayList(
                "Nombre total de commandes : " + commandes.size(),
                "Menu le plus pris : " + getMostOrderedMenu(menuCount),
                "Menu le moins pris : " + getLeastOrderedMenu(menuCount),
                "Type de plat le plus populaire : " + getMostPopularType(menuCount),
                "Recette totale générée : " + String.format("%.2f", calculateTotalRevenue()) + " €",
                "Recette moyenne par commande : " + String.format("%.2f", calculateAverageRevenuePerOrder()) + " €",
                "Menu le plus rentable : " + getMostProfitableMenu()
        );

        // Création du graphique en barres
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Nombre de Commandes par Menu");
        xAxis.setLabel("Menus");
        yAxis.setLabel("Nombre de Commandes");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes");

        for (Menu menu : menus) {
            int count = menuCount.getOrDefault(menu.getName(), 0);
            series.getData().add(new XYChart.Data<>(menu.getName(), count));
        }

        barChart.getData().add(series);

        // TableView pour afficher les menus
        TableView<Menu> tableView = new TableView<>();
        TableColumn<Menu, String> column1 = new TableColumn<>("Menu");
        column1.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        column1.setMinWidth(150);

        TableColumn<Menu, String> column2 = new TableColumn<>("Items");
        column2.setCellValueFactory(cellData -> cellData.getValue().getItemsProperty());
        column2.setMinWidth(250);

        TableColumn<Menu, String> column3 = new TableColumn<>("Prix");
        column3.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice()) + " €"));
        column3.setMinWidth(100);

        TableColumn<Menu, Integer> column4 = new TableColumn<>("Nombre de Commandes");
        column4.setCellValueFactory(cellData -> new SimpleIntegerProperty(menuCount.getOrDefault(cellData.getValue().getName(), 0)).asObject());
        column4.setMinWidth(150);

        tableView.getColumns().addAll(column1, column2, column3, column4);
        tableView.setItems(menuList);

        // ListView pour afficher les statistiques
        ListView<String> statsListView = new ListView<>(stats);
        statsListView.setMaxHeight(200);

        // Création du conteneur scrollable
        VBox vbox = new VBox(20, tableView, statsListView, barChart);
        vbox.setPadding(new Insets(15, 20, 15, 20));

        // Ajout du ScrollPane pour scroller
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        // Conteneur principal avec bordures façon iPad
        StackPane stackPane = new StackPane();
        stackPane.setStyle(
                "-fx-background-color: black; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 50px; " +
                        "-fx-border-radius: 50px; " +
                        "-fx-background-radius: 50px;"
        );

        // Conteneur du contenu blanc
        StackPane contentPane = new StackPane(scrollPane);
        contentPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 30px;"
        );

        stackPane.getChildren().add(contentPane);

        // Bouton "Accueil" façon iPad
        Button homeButton = new Button("⬤");
        homeButton.setStyle(
                "-fx-background-color: gray; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 10px; " +
                        "-fx-background-radius: 50%; " +
                        "-fx-min-width: 10px; " +
                        "-fx-min-height: 10px;"
        );

        // Positionner le bouton sur la bordure
        StackPane.setAlignment(homeButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(homeButton, new Insets(0, 0, -30, 0));

        stackPane.getChildren().add(homeButton);

        // Définir la scène
        Scene scene = new Scene(stackPane, 850, 650);
        primaryStage.setTitle("Dashboard des Menus - Style iPad");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Méthodes pour les statistiques
    private String getMostOrderedMenu(Map<String, Integer> menuCount) {
        return menuCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun menu");
    }

    private String getLeastOrderedMenu(Map<String, Integer> menuCount) {
        return menuCount.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun menu");
    }

    private String getMostPopularType(Map<String, Integer> menuCount) {
        Map<String, Integer> typeCount = new HashMap<>();
        for (Map.Entry<String, Integer> entry : menuCount.entrySet()) {
            String menuName = entry.getKey();
            Menu menu = findMenuByName(menuName);
            if (menu != null) {
                String type = menu.getType();
                typeCount.put(type, typeCount.getOrDefault(type, 0) + entry.getValue());
            }
        }
        return typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun type");
    }

    private String getMostProfitableMenu() {
        return menus.stream()
                .max(Comparator.comparingDouble(Menu::getPrice))
                .map(Menu::getName)
                .orElse("Aucun menu");
    }

    private Menu findMenuByName(String name) {
        return menus.stream().filter(menu -> menu.getName().equals(name)).findFirst().orElse(null);
    }

    private double calculateTotalRevenue() {
        return commandes.stream()
                .flatMap(commande -> commande.getItems().stream())
                .mapToDouble(Menu::getPrice)
                .sum();
    }

    private double calculateAverageRevenuePerOrder() {
        long totalOrders = commandes.size();
        return totalOrders > 0 ? calculateTotalRevenue() / totalOrders : 0;
    }
}



