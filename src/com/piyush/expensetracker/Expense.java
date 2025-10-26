package com.piyush.expensetracker;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Expense {
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty amount;
    private final SimpleObjectProperty<LocalDate> date;

    public Expense(String name, double amount, LocalDate date) {
        this.name = new SimpleStringProperty(name);
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
    }

    public String getName() { return name.get(); }
    public double getAmount() { return amount.get(); }
    public LocalDate getDate() { return date.get(); }

    public void setName(String name) { this.name.set(name); }
    public void setAmount(double amount) { this.amount.set(amount); }
    public void setDate(LocalDate date) { this.date.set(date); }
}
