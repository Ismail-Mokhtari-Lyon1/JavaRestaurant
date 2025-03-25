package com.restaurant1.demo;
import java.util.ArrayList;
import java.util.List;
// Classe Commande
public class Commande {
    private List<Menu> items = new ArrayList<>();

    public void ajouterMenu(Menu menu) {
        items.add(menu);
    }

    public List<Menu> getItems() {
        return items;
    }
}