package com.example.zozo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    private List<Menu> items = new ArrayList<>();
    private LocalTime heure;
    private LocalDate date;  // Ajout de l'attribut date

    public void ajouterMenu(Menu menu) {
        items.add(menu);
    }

    public List<Menu> getItems() {
        return items;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
