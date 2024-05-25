import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class OrderPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;
    private ObservableList<OrderItem> basket;

    public OrderPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.basket = FXCollections.observableArrayList();
        this.view = createView();
    }

    public GridPane getView() {
        return view;
    }

    private GridPane createView() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20px; -fx-background-color: #f5f5f5;");

        Label itemLabel = new Label("Select Item:");
        itemLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        grid.add(itemLabel, 0, 0);

        ObservableList<String> options = FXCollections.observableArrayList("Burrito", "Fries", "Soda");
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setStyle("-fx-pref-width: 150px; -fx-font-size: 14px;");
        grid.add(comboBox, 1, 0);

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        grid.add(quantityLabel, 0, 1);

        TextField quantityField = new TextField();
        quantityField.setStyle("-fx-pref-width: 150px; -fx-font-size: 14px;");
        grid.add(quantityField, 1, 1);

        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addToBasketButton.setOnAction(e -> handleAddToBasket(comboBox, quantityField));
        grid.add(addToBasketButton, 1, 2);

        // Display basket
        ListView<OrderItem> basketView = new ListView<>(basket);
        basketView.setPrefHeight(200);
        basketView.setStyle("-fx-pref-width: 300px; -fx-font-size: 14px;");
        grid.add(new Label("Basket:"), 0, 3);
        grid.add(basketView, 1, 3);

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        checkoutButton.setOnAction(e -> handleCheckout(comboBox, quantityField));
        grid.add(checkoutButton, 1, 4);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView())));
        grid.add(backButton, 1, 5);

        return grid;
    }

    private void handleAddToBasket(ComboBox<String> comboBox, TextField quantityField) {
        String item = comboBox.getValue();
        String quantity = quantityField.getText();
        if (item != null && !quantity.isEmpty()) {
            try {
                int qty = Integer.parseInt(quantity);
                basket.add(new OrderItem(item, qty));
            } catch (NumberFormatException e) {
                showAlert("Invalid Quantity", "Please enter a valid number for quantity.");
            }
        } else {
            showAlert("Invalid Input", "Please select an item and enter a quantity.");
        }
    }

    private void handleCheckout(ComboBox<String> comboBox, TextField quantityField) {
        if (!basket.isEmpty()) {
            OrderData.orders.addAll(basket);
            comboBox.getSelectionModel().clearSelection();
            quantityField.clear();
            basket.clear();
            showAlert("Order Placed", "Your order has been placed successfully.");
        } else {
            showAlert("Empty Basket", "Your basket is empty. Please add items to the basket.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class OrderItem {
        private String item;
        private int quantity;

        public OrderItem(String item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public String getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        @Override
        public String toString() {
            return item + " - Quantity: " + quantity;
        }
    }
}