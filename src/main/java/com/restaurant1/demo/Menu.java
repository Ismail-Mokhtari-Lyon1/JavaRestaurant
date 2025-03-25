package com.restaurant1.demo;

import javafx.beans.property.SimpleStringProperty;

// Classe Menu
public class Menu {
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

