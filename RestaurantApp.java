
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Insets;

import java.util.*;

public class RestaurantApp extends Application {
    private static List<Menu> menus = new ArrayList<>();
    private static List<Commande> commandes = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);  // Lance l'application JavaFX
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialiser les menus avec des items et prix
        menus.add(new Menu("Menu 1", "Coca-Cola, Steak frites, Tiramisu", 20.00));
        menus.add(new Menu("Menu 2", "Coca-Cola, Salade César, Tiramisu", 18.00));
        menus.add(new Menu("Menu 3", "Coca-Cola, Steak frites, Salade César", 22.00));
        menus.add(new Menu("Menu 4", "Coca-Cola, Tiramisu, Salade César", 16.50));



        // Simuler quelques commandes
        Commande commande1 = new Commande();
        commande1.ajouterMenu(menus.get(0));  // Menu 1
        commande1.ajouterMenu(menus.get(2));  // Menu 3

        Commande commande2 = new Commande();
        commande2.ajouterMenu(menus.get(1));  // Menu 2
        commande2.ajouterMenu(menus.get(0));  // Menu 1

        commandes.add(commande1);
        commandes.add(commande2);

        // Compter le nombre de fois que chaque menu est choisi
        Map<String, Integer> menuCount = new HashMap<>();
        for (Commande commande : commandes) {
            for (Menu menu : commande.getItems()) {
                menuCount.put(menu.getName(), menuCount.getOrDefault(menu.getName(), 0) + 1);
            }
        }

        // Créer une liste d'éléments à afficher dans le tableau
        ObservableList<Menu> menuList = FXCollections.observableArrayList(menus);
        ObservableList<String> stats = FXCollections.observableArrayList();

        // Ajouter les stats
        stats.add("Nombre total de commandes : " + commandes.size());
        stats.add("Menu le plus pris : " + getMostOrderedMenu(menuCount));
        stats.add("Menu le moins pris : " + getLeastOrderedMenu(menuCount));
        stats.add("Type de plat le plus populaire : " + getMostPopularType(menuCount));
        stats.add("Recette totale générée : " + String.format("%.2f", calculateTotalRevenue()) + " €");
        stats.add("Recette moyenne par commande : " + String.format("%.2f", calculateAverageRevenuePerOrder()) + " €");
        stats.add("Menu le plus rentable : " + getMostProfitableMenu());

        // Créer un graphique en barre
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Nombre de Commandes par Menu");
        xAxis.setLabel("Menus");
        yAxis.setLabel("Nombre de Commandes");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes");

        // Ajouter les données de commandes dans le graphique
        for (Menu menu : menus) {
            int count = menuCount.getOrDefault(menu.getName(), 0);
            series.getData().add(new XYChart.Data<>(menu.getName(), count));
        }

        barChart.getData().add(series);

        // Créer une TableView pour afficher les menus
        TableView<Menu> tableView = new TableView<>();
        TableColumn<Menu, String> column1 = new TableColumn<>("Menu");
        column1.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        column1.setMinWidth(150);  // Limiter la largeur de cette colonne

        TableColumn<Menu, String> column2 = new TableColumn<>("Items");
        column2.setCellValueFactory(cellData -> cellData.getValue().getItemsProperty());
        column2.setMinWidth(250);  // Limiter la largeur de cette colonne

        TableColumn<Menu, String> column3 = new TableColumn<>("Prix");
        column3.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice()) + " €"));
        column3.setMinWidth(100);  // Limiter la largeur de cette colonne

        TableColumn<Menu, Integer> column4 = new TableColumn<>("Nombre de Commandes");
        column4.setCellValueFactory(cellData -> new SimpleIntegerProperty(menuCount.getOrDefault(cellData.getValue().getName(), 0)).asObject());
        column4.setMinWidth(150);  // Limiter la largeur de cette colonne


// Empêcher l'ajout d'une colonne vide en vous assurant que ces colonnes sont ajoutées correctement
        tableView.getColumns().addAll(column1, column2, column3, column4);

// Appliquer la politique de redimensionnement des colonnes
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.getColumns().addAll(column1, column2, column3, column4);
        tableView.setItems(menuList);



        // Créer un ListView pour afficher les statistiques
        ListView<String> statsListView = new ListView<>(stats);

        // Créer des titres pour organiser les sections
        Label tableTitle = new Label("Tableau des Menus");
        tableTitle.setFont(Font.font("Arial", 18));
        Label summaryTitle = new Label("Résumé");
        summaryTitle.setFont(Font.font("Arial", 18));

        // Ajouter un style au tableau, aux titres et au graphique
        tableTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        summaryTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        statsListView.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        statsListView.setMaxHeight(200);

        // Ajouter un effet d'ombre portée sur les éléments graphiques
        tableView.setEffect(new DropShadow(10, Color.GRAY));
        barChart.setEffect(new DropShadow(10, Color.GRAY));

        // Ajouter des marges et des espacements
        VBox vbox = new VBox(10, tableTitle, tableView, summaryTitle, statsListView, barChart);
        vbox.setPadding(new Insets(15, 20, 15, 20));
        vbox.setStyle("-fx-background-color: #f4f4f4;");

        // Créer la scène et l'ajouter au stage
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setTitle("Dashboard des Menus");
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
        // Compter le type de plat le plus populaire
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

// Classe Menu
class Menu {
    private SimpleStringProperty name;
    private SimpleStringProperty items;
    private String type;
    private double price;

    public Menu(String name, String items, double price) {
        this.name = new SimpleStringProperty(name);
        this.items = new SimpleStringProperty(items);
        this.type = "Menu";
        this.price = price;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty getNameProperty() {
        return name;
    }

    public String getItems() {
        return items.get();
    }

    public SimpleStringProperty getItemsProperty() {
        return items;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }
}

// Classe Commande
class Commande {
    private List<Menu> items = new ArrayList<>();

    public void ajouterMenu(Menu menu) {
        items.add(menu);
    }

    public List<Menu> getItems() {
        return items;
    }
}
