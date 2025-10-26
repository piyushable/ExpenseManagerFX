package com.piyush.expensetracker;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;

public class ExpenseManagerFX extends Application {
    private boolean sortAscending = true;

    @Override
    public void start(Stage stage) {
        ObservableList<Expense> expenseList = FXCollections.observableArrayList();
        FilteredList<Expense> filteredList = new FilteredList<>(expenseList, p -> true);

        // ─── TableView ─────────────────────────────
        TableView<Expense> table = new TableView<>();
        TableColumn<Expense, String> nameCol = new TableColumn<>("Expense Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount (₹)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(nameCol, amountCol, dateCol);
        table.setItems(filteredList);

        // Better column sizing
        nameCol.setPrefWidth(250);
        amountCol.setPrefWidth(120);
        dateCol.setPrefWidth(120);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // ─── Sidebar (Date List) ─────────────────────────────
        ListView<LocalDate> dateListView = new ListView<>();
        dateListView.setPlaceholder(new Label("No Dates Yet"));

        Button sortButton = new Button("⇅ Sort Dates");
        sortButton.setStyle("-fx-background-color: #0078d7; -fx-text-fill: white;");

        sortButton.setOnAction(e -> {
            sortAscending = !sortAscending;
            dateListView.getItems().sort(sortAscending
                    ? Comparator.naturalOrder()
                    : Comparator.reverseOrder());
        });

        VBox sidebarBox = new VBox(8, sortButton, dateListView);
        sidebarBox.setPadding(new Insets(10));

        // ─── Header (Title + Summary) ─────────────────────────────
        Label titleLabel = new Label("Expense Manager");
        titleLabel.setId("title");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label summaryLabel = new Label("Select a date to see total spending");
        summaryLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        VBox headerBox = new VBox(2);
        headerBox.getChildren().addAll(titleLabel, summaryLabel);
        headerBox.setPadding(new Insets(10, 20, 10, 20));
        headerBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        headerBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        headerBox.setEffect(new DropShadow(2, Color.rgb(0, 0, 0, 0.05)));

        // ─── Input Toolbar ─────────────────────────────
        TextField expenseNameField = new TextField();
        expenseNameField.setPromptText("Expense Name");

        TextField expenseAmountField = new TextField();
        expenseAmountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

        Button addButton = new Button("Add Expense");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");

        HBox inputBox = new HBox(10, expenseNameField, expenseAmountField, datePicker, addButton, deleteButton);
        inputBox.setPadding(new Insets(10));
        inputBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(6), Insets.EMPTY)));
        inputBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));
        inputBox.setEffect(new DropShadow(2, Color.rgb(0, 0, 0, 0.05)));

        // ─── SplitPane Layout ─────────────────────────────
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sidebarBox, table);
        splitPane.setDividerPositions(0.3);

        // ─── Add Expense ─────────────────────────────
        addButton.setOnAction(e -> {
            String name = expenseNameField.getText().trim();
            String amountText = expenseAmountField.getText().trim();
            LocalDate date = datePicker.getValue();

            if (name.isEmpty() || amountText.isEmpty() || date == null) {
                new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                Expense newExp = new Expense(name, amount, date);
                expenseList.add(newExp);

                // Add date if not already there
                if (!dateListView.getItems().contains(date)) {
                    dateListView.getItems().add(date);
                    dateListView.getItems().sort(sortAscending
                            ? Comparator.naturalOrder()
                            : Comparator.reverseOrder());
                }

                // Clear fields
                expenseNameField.clear();
                expenseAmountField.clear();
                datePicker.setValue(null);

                // Update total if same date selected
                LocalDate selected = dateListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    filteredList.setPredicate(exp -> exp.getDate().equals(selected));
                    double total = expenseList.stream()
                            .filter(exp -> exp.getDate().equals(selected))
                            .mapToDouble(Expense::getAmount)
                            .sum();
                    summaryLabel.setText("Total spent on " + selected + ": ₹" + total);
                } else {
                    filteredList.setPredicate(p -> true);
                }

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Please enter a valid number for amount!").show();
            }
        });

        // ─── Delete Expense or Entire Date ─────────────────────────────
        deleteButton.setOnAction(e -> {
            Expense selectedExpense = table.getSelectionModel().getSelectedItem();
            LocalDate selectedDate = dateListView.getSelectionModel().getSelectedItem();

            if (selectedExpense != null) {
                // Delete only the selected expense
                LocalDate dateOfRemoved = selectedExpense.getDate();
                boolean removed = expenseList.remove(selectedExpense);
                if (removed) {
                    boolean stillHasDate = expenseList.stream().anyMatch(exp -> exp.getDate().equals(dateOfRemoved));
                    if (!stillHasDate) {
                        dateListView.getItems().remove(dateOfRemoved);
                    }
                    // Update total after deletion
                    if (selectedDate != null && selectedDate.equals(dateOfRemoved)) {
                        double total = expenseList.stream()
                                .filter(exp -> exp.getDate().equals(selectedDate))
                                .mapToDouble(Expense::getAmount)
                                .sum();
                        summaryLabel.setText("Total spent on " + selectedDate + ": ₹" + total);
                    }
                }
            } else if (selectedDate != null) {
                // Delete all expenses for that date
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete All Expenses");
                confirm.setHeaderText(null);
                confirm.setContentText("Are you sure you want to delete all expenses for " + selectedDate + "?");
                confirm.showAndWait();

                if (confirm.getResult() == ButtonType.OK) {
                    expenseList.removeIf(exp -> exp.getDate().equals(selectedDate));
                    dateListView.getItems().remove(selectedDate);
                    filteredList.setPredicate(p -> true);
                    summaryLabel.setText("Deleted all expenses for " + selectedDate);
                }
            } else {
                // Nothing selected
                new Alert(Alert.AlertType.INFORMATION, "Please select an expense or a date to delete.").show();
            }
        });

        // ─── Filter by Date ─────────────────────────────
        dateListView.getSelectionModel().selectedItemProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                filteredList.setPredicate(exp -> exp.getDate().equals(newDate));
                double total = expenseList.stream()
                        .filter(exp -> exp.getDate().equals(newDate))
                        .mapToDouble(Expense::getAmount)
                        .sum();
                summaryLabel.setText("Total spent on " + newDate + ": ₹" + total);
            } else {
                filteredList.setPredicate(p -> true);
                summaryLabel.setText("Select a date to see total spending");
            }
        });

        // ─── Root Layout ─────────────────────────────
        BorderPane root = new BorderPane();
        root.setTop(headerBox);
        root.setCenter(splitPane);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 850, 520);
        stage.setScene(scene);
        stage.setTitle("Expense Manager");
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
