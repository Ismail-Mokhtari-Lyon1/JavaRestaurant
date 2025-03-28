package com.example.zozo;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

public class Statistiques {

    public static Scene getStatScene(Stage primaryStage, Runnable onBack) {
        // --- Création du titre ---
        Label globalTitle = new Label("Dashboard Statistiques");
        globalTitle.getStyleClass().add("title-label");
        HBox titleBox = new HBox(globalTitle);
        titleBox.getStyleClass().add("title-box");
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        titleBox.setAlignment(Pos.CENTER);

        // --- Création du filtre, placé sous le titre ---
        Label filterLabel = new Label("Afficher par :");
        filterLabel.getStyleClass().add("sidebar-label");
        ComboBox<String> afficherParCombo = new ComboBox<>();
        afficherParCombo.getItems().addAll("Jour", "Semaine", "Mois");
        afficherParCombo.setValue("Jour");
        afficherParCombo.getStyleClass().add("sidebar-combo");
        VBox filterBox = new VBox(10, filterLabel, afficherParCombo);
        filterBox.setAlignment(Pos.CENTER);

        // --- Création du header complet (titre + filtre en dessous) ---
        VBox header = new VBox(10, titleBox, filterBox);
        header.setStyle("-fx-background-color: #496474; -fx-padding: 20;");
        header.setAlignment(Pos.CENTER);

        // === KPI CA Total (mis en évidence) ===
        Label totalRevenueLabel = new Label("CA Total : 0,00 €");
        totalRevenueLabel.getStyleClass().add("kpi-ca");
        HBox revenueBox = new HBox(totalRevenueLabel);
        revenueBox.setAlignment(Pos.CENTER);

        // === Graphique en camembert (PieChart) ===
        PieChart pieChart = new PieChart();
        pieChart.getStyleClass().addAll("chart", "pie-chart");
        pieChart.setTitle("Répartition des commandes par menu");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setPrefWidth(600);

        // === KPI menus (plus commandé / moins commandé) ===
        VBox kpiMostOrdered = new VBox(5);
        kpiMostOrdered.getStyleClass().add("kpi-most");
        kpiMostOrdered.setAlignment(Pos.CENTER);
        Label mostOrderedTitle = new Label("Menu le plus commandé");
        mostOrderedTitle.getStyleClass().add("kpi-title");
        Label kpiMostOrderedValue = new Label("");
        kpiMostOrderedValue.getStyleClass().add("kpi-value");
        kpiMostOrdered.getChildren().addAll(mostOrderedTitle, kpiMostOrderedValue);

        VBox kpiLeastOrdered = new VBox(5);
        kpiLeastOrdered.getStyleClass().add("kpi-least");
        kpiLeastOrdered.setAlignment(Pos.CENTER);
        Label leastOrderedTitle = new Label("Menu le moins commandé");
        leastOrderedTitle.getStyleClass().add("kpi-title");
        Label kpiLeastOrderedValue = new Label("");
        kpiLeastOrderedValue.getStyleClass().add("kpi-value");
        kpiLeastOrdered.getChildren().addAll(leastOrderedTitle, kpiLeastOrderedValue);

        VBox kpiBox = new VBox(20, kpiMostOrdered, kpiLeastOrdered);
        kpiBox.getStyleClass().add("kpi-box");
        kpiBox.setAlignment(Pos.CENTER);
        kpiBox.setPrefWidth(250);

        // Graphique camembert + KPI côte à côte
        HBox mainContentTop = new HBox(20, pieChart, kpiBox);
        mainContentTop.setAlignment(Pos.CENTER);

        // === Graphique en courbe horaire ===
        CategoryAxis hourXAxis = new CategoryAxis();
        String[] hourCategories = new String[12];
        for (int h = 11, i = 0; h < 23; h++, i++) {
            hourCategories[i] = h + "-" + (h + 1) + "h";
        }
        hourXAxis.setCategories(FXCollections.observableArrayList(hourCategories));
        hourXAxis.setLabel("Tranche horaire");

        NumberAxis hourYAxis = new NumberAxis();
        hourYAxis.setLabel("Total menus commandés");

        LineChart<String, Number> hourlyChart = new LineChart<>(hourXAxis, hourYAxis);
        hourlyChart.getStyleClass().addAll("chart", "line-chart");
        hourlyChart.setTitle("Commandes par tranche horaire");
        hourlyChart.setLegendVisible(false);
        hourlyChart.setPrefWidth(600);

        // Filtre pour le graphique horaire (ComboBox "Tous", "Menu 1", "Menu 2", "Menu 3", "Menu 4")
        Label hourlyFilterLabel = new Label("Filtrer hourly par menu :");
        ComboBox<String> menuFilterComboBox = new ComboBox<>();
        menuFilterComboBox.getItems().addAll("Tous", "Menu 1", "Menu 2", "Menu 3", "Menu 4");
        menuFilterComboBox.setValue("Tous");

        HBox hourlyFilterBox = new HBox(10, hourlyFilterLabel, menuFilterComboBox);
        hourlyFilterBox.setAlignment(Pos.TOP_RIGHT);
        hourlyFilterBox.setPadding(new Insets(0, 10, 10, 0));

        BorderPane hourlyPane = new BorderPane();
        hourlyPane.setCenter(hourlyChart);
        hourlyPane.setTop(hourlyFilterBox);

        // === Troisième graphique : Panier moyen (BarChart) ===
        CategoryAxis basketXAxis = new CategoryAxis();
        NumberAxis basketYAxis = new NumberAxis();
        basketYAxis.setLabel("Panier moyen (€)");
        BarChart<String, Number> basketChart = new BarChart<>(basketXAxis, basketYAxis);
        basketChart.getStyleClass().addAll("chart", "bar-chart");
        basketChart.setTitle("Panier moyen");
        basketChart.setLegendVisible(false);
        basketChart.setPrefWidth(600);

        // === Boutons en bas ===
        Button refreshButton = new Button("Rafraîchir");
        refreshButton.getStyleClass().add("dashboard-button");
        Button stocksButton = new Button("Accéder aux stocks");
        stocksButton.getStyleClass().add("dashboard-button");
        Button retourButton = new Button("Retour");
        retourButton.getStyleClass().add("dashboard-button");

        HBox buttonBox = new HBox(20, refreshButton, stocksButton, retourButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 20, 0));

        // Contenu principal central
        VBox contentMain = new VBox(20, mainContentTop, hourlyPane, basketChart, buttonBox);
        contentMain.setAlignment(Pos.CENTER);

        // Assemblage global dans un BorderPane
        BorderPane mainPane = new BorderPane();
        // Le header (titre + filtre en dessous) et le KPI CA sont en haut
        VBox topBox = new VBox(0, header, revenueBox);
        topBox.setAlignment(Pos.CENTER);
        mainPane.setTop(topBox);
        // Le contenu central reste dans le centre
        VBox centerBox = new VBox(20, contentMain);
        centerBox.setAlignment(Pos.TOP_CENTER);
        mainPane.setCenter(centerBox);
        BorderPane.setMargin(centerBox, new Insets(20));

        // Envelopper mainPane dans un ScrollPane pour le défilement
        ScrollPane scrollPane = new ScrollPane(mainPane);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        // === Listeners ===
        afficherParCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalRevenueLabel(totalRevenueLabel, afficherParCombo.getValue());
            updatePieChart(pieChart, afficherParCombo.getValue(), kpiMostOrderedValue, kpiLeastOrderedValue);
            updateHourlyChart(hourlyChart, afficherParCombo.getValue(), menuFilterComboBox.getValue());
            updateBasketChart(basketChart, afficherParCombo.getValue());
        });
        menuFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateHourlyChart(hourlyChart, afficherParCombo.getValue(), menuFilterComboBox.getValue());
        });
        refreshButton.setOnAction(e -> {
            updateTotalRevenueLabel(totalRevenueLabel, afficherParCombo.getValue());
            updatePieChart(pieChart, afficherParCombo.getValue(), kpiMostOrderedValue, kpiLeastOrderedValue);
            updateHourlyChart(hourlyChart, afficherParCombo.getValue(), menuFilterComboBox.getValue());
            updateBasketChart(basketChart, afficherParCombo.getValue());
        });
        retourButton.setOnAction(e -> onBack.run());
        stocksButton.setOnAction(e -> {
            primaryStage.setScene(Stocks.getStocksScene(primaryStage, () -> primaryStage.setScene(getStatScene(primaryStage, onBack))));
        });

        // Mise à jour initiale
        updateTotalRevenueLabel(totalRevenueLabel, afficherParCombo.getValue());
        updatePieChart(pieChart, afficherParCombo.getValue(), kpiMostOrderedValue, kpiLeastOrderedValue);
        updateHourlyChart(hourlyChart, afficherParCombo.getValue(), menuFilterComboBox.getValue());
        updateBasketChart(basketChart, afficherParCombo.getValue());

        Scene scene = new Scene(scrollPane, 700, 1200);
        scene.getStylesheets().add(Statistiques.class.getResource("statistiques.css").toExternalForm());
        scrollPane.getStyleClass().add("root");
        return scene;
    }

    // === Mise à jour du KPI CA Total ===
    private static void updateTotalRevenueLabel(Label revenueLabel, String afficherPar) {
        double totalRevenue = 0.0;
        List<Commande> filtered = getFilteredOrders(afficherPar);
        for (Commande cmd : filtered) {
            for (Menu m : cmd.getItems()) {
                totalRevenue += m.getPrice();
            }
        }
        revenueLabel.setText(String.format("CA Total : %.2f €", totalRevenue));
    }

    // === Mise à jour du PieChart et des KPI (menus les plus/moins commandés) ===
    private static void updatePieChart(PieChart pieChart, String afficherPar,
                                       Label kpiMostOrderedValue, Label kpiLeastOrderedValue) {
        pieChart.getData().clear();
        Map<String, Integer> aggregatedData = aggregateMenus(afficherPar);
        int totalMenus = aggregatedData.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : aggregatedData.entrySet()) {
            String menuName = entry.getKey();
            int count = entry.getValue();
            double percent = totalMenus > 0 ? (count * 100.0) / totalMenus : 0;
            PieChart.Data data = new PieChart.Data(menuName + " (" + String.format("%.1f", percent) + "%)", count);
            pieChart.getData().add(data);
            double price = getMenuPrice(menuName);
            double totalEuro = count * price;
            String tooltipText = String.format("Total: %.2f €", totalEuro);
            Tooltip tooltip = new Tooltip(tooltipText);
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, tooltip);
                }
            });
        }
        String mostOrdered = aggregatedData.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("Aucun");
        String leastOrdered = aggregatedData.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("Aucun");
        kpiMostOrderedValue.setText(mostOrdered);
        kpiLeastOrderedValue.setText(leastOrdered);
    }

    // === Helper pour obtenir le prix d'un menu depuis RestaurantApp.menus ===
    private static double getMenuPrice(String menuName) {
        for (Menu m : RestaurantApp.menus) {
            if (m.getName().equals(menuName)) {
                return m.getPrice();
            }
        }
        return 0.0;
    }

    // === Mise à jour du graphique en courbe horaire ===
    private static void updateHourlyChart(LineChart<String, Number> hourlyChart, String afficherPar, String menuFilter) {
        hourlyChart.getData().clear();
        Map<String, Integer> hourlyCounts = new LinkedHashMap<>();
        for (int h = 11; h < 23; h++) {
            String category = h + "-" + (h + 1) + "h";
            hourlyCounts.put(category, 0);
        }
        List<Commande> filtered = getFilteredOrders(afficherPar);
        for (Commande cmd : filtered) {
            int orderHour = cmd.getHeure().getHour();
            if (orderHour >= 11 && orderHour < 23) {
                String category = orderHour + "-" + (orderHour + 1) + "h";
                int count = 0;
                if (menuFilter.equals("Tous")) {
                    count = cmd.getItems().size();
                } else {
                    for (Menu m : cmd.getItems()) {
                        if (m.getName().equals(menuFilter)) {
                            count++;
                        }
                    }
                }
                hourlyCounts.put(category, hourlyCounts.get(category) + count);
            }
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes par tranche horaire");
        hourlyCounts.forEach((category, count) -> series.getData().add(new XYChart.Data<>(category, count)));
        hourlyChart.getData().add(series);
    }

    // === Mise à jour du graphique du panier moyen (BarChart) ===
    private static void updateBasketChart(BarChart<String, Number> basketChart, String afficherPar) {
        basketChart.getData().clear();
        // Réduire la largeur de la barre en mode "Jour"
        if (afficherPar.equals("Jour")) {
            basketChart.setCategoryGap(300);
        } else {
            basketChart.setCategoryGap(10);
        }
        Map<String, List<Double>> groupTotals = new LinkedHashMap<>();
        List<Commande> filtered = getFilteredOrders(afficherPar);

        if (afficherPar.equals("Jour")) {
            double sum = 0;
            int count = 0;
            for (Commande cmd : filtered) {
                double total = 0;
                for (Menu m : cmd.getItems()) {
                    total += m.getPrice();
                }
                sum += total;
                count++;
            }
            double avg = count > 0 ? sum / count : 0;
            groupTotals.put(LocalDate.now().toString(), Arrays.asList(avg));
        } else if (afficherPar.equals("Semaine")) {
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(DayOfWeek.MONDAY);
            for (int i = 0; i < 7; i++) {
                LocalDate day = monday.plusDays(i);
                groupTotals.put(day.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH), new ArrayList<>());
            }
            for (Commande cmd : filtered) {
                String dayName = cmd.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
                double total = 0;
                for (Menu m : cmd.getItems()) {
                    total += m.getPrice();
                }
                if (groupTotals.containsKey(dayName)) {
                    groupTotals.get(dayName).add(total);
                }
            }
        } else if (afficherPar.equals("Mois")) {
            YearMonth ym = YearMonth.from(LocalDate.now());
            int length = ym.lengthOfMonth();
            int totalWeeks = (length + 6) / 7;
            for (int week = 1; week <= totalWeeks; week++) {
                groupTotals.put("Semaine " + week, new ArrayList<>());
            }
            for (Commande cmd : filtered) {
                int day = cmd.getDate().getDayOfMonth();
                int week = ((day - 1) / 7) + 1;
                double total = 0;
                for (Menu m : cmd.getItems()) {
                    total += m.getPrice();
                }
                groupTotals.get("Semaine " + week).add(total);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Panier moyen");
        for (Map.Entry<String, List<Double>> entry : groupTotals.entrySet()) {
            List<Double> totals = entry.getValue();
            double avg = totals.isEmpty() ? 0 : totals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            series.getData().add(new XYChart.Data<>(entry.getKey(), avg));
        }
        basketChart.getData().add(series);
    }

    // === Helper pour filtrer les commandes selon le filtre "Afficher par" ===
    private static List<Commande> getFilteredOrders(String afficherPar) {
        LocalDate now = LocalDate.now();
        List<Commande> filtered = new ArrayList<>();
        switch (afficherPar) {
            case "Jour":
                for (Commande cmd : RestaurantApp.commandes) {
                    if (cmd.getDate() != null && cmd.getDate().equals(now)) {
                        filtered.add(cmd);
                    }
                }
                break;
            case "Semaine":
                LocalDate monday = now.with(DayOfWeek.MONDAY);
                LocalDate sunday = monday.with(DayOfWeek.SUNDAY);
                for (Commande cmd : RestaurantApp.commandes) {
                    if (cmd.getDate() != null && !cmd.getDate().isBefore(monday) && !cmd.getDate().isAfter(sunday)) {
                        filtered.add(cmd);
                    }
                }
                break;
            case "Mois":
                int month = now.getMonthValue();
                int year = now.getYear();
                for (Commande cmd : RestaurantApp.commandes) {
                    if (cmd.getDate() != null && cmd.getDate().getMonthValue() == month && cmd.getDate().getYear() == year) {
                        filtered.add(cmd);
                    }
                }
                break;
        }
        return filtered;
    }

    // === Agrégation pour le PieChart (répartition des menus) ===
    private static Map<String, Integer> aggregateMenus(String afficherPar) {
        List<Commande> filtered = getFilteredOrders(afficherPar);
        Map<String, Integer> aggregated = new LinkedHashMap<>();
        for (Commande cmd : filtered) {
            for (Menu menu : cmd.getItems()) {
                aggregated.put(menu.getName(), aggregated.getOrDefault(menu.getName(), 0) + 1);
            }
        }
        return aggregated;
    }
}
