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
    private ComboBox<String> comboBox;
    private TextField quantityField;
    private Label totalLabel;

    public OrderPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.basket = FXCollections.observableArrayList();
        this.view = createView();
        // Set the title and full screen mode
        primaryStage.setTitle("Order");
        primaryStage.setScene(new Scene(view));
        primaryStage.setFullScreen(true);
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
        comboBox = new ComboBox<>(options);
        comboBox.setStyle("-fx-pref-width: 150px; -fx-font-size: 14px;");
        grid.add(comboBox, 1, 0);

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        grid.add(quantityLabel, 0, 1);

        quantityField = new TextField();
        quantityField.setStyle("-fx-pref-width: 150px; -fx-font-size: 14px;");
        grid.add(quantityField, 1, 1);

        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        addToBasketButton.setOnAction(e -> handleAddToBasket());
        grid.add(addToBasketButton, 1, 2);

        // Total price label
        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        grid.add(totalLabel, 1, 3);

        // Display basket
        ListView<OrderItem> basketView = new ListView<>(basket);
        basketView.setPrefHeight(200);
        basketView.setStyle("-fx-pref-width: 300px; -fx-font-size: 14px;");
        grid.add(new Label("Basket:"), 0, 4);
        grid.add(basketView, 1, 4);

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px;");
        checkoutButton.setOnAction(e -> handleCheckout());
        grid.add(checkoutButton, 1, 5);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(
                e -> {
                    DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
                    primaryStage.setTitle("Dashboard");
                    primaryStage.setScene(new Scene(dashboardPage.getView()));
                    primaryStage.setFullScreen(true);
                });
        grid.add(backButton, 1, 6);

        return grid;
    }

    private void handleAddToBasket() {
        String item = comboBox.getValue();
        String quantityText = quantityField.getText();
        if (item != null && !quantityText.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityText);
                double price = getItemPrice(item);
                basket.add(new OrderItem(item, quantity, price));
                updateTotal();
            } catch (NumberFormatException e) {
                showAlert("Invalid Quantity", "Please enter a valid number for quantity.");
            }
        } else {
            showAlert("Invalid Input", "Please select an item and enter a quantity.");
        }
    }

    private void handleCheckout() {
        if (!basket.isEmpty()) {
            OrderData.orders.addAll(basket);
            comboBox.getSelectionModel().clearSelection();
            quantityField.clear();
            basket.clear();
            updateTotal();
            showAlert("Order Placed", "Your order has been placed successfully.");
        } else {
            showAlert("Empty Basket", "Your basket is empty. Please add items to the basket.");
        }
    }

    private void updateTotal() {
        double total = 0;
        for (OrderItem item : basket) {
            total += item.getTotalPrice();
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private double getItemPrice(String item) {
        switch (item) {
            case "Burrito":
                return 15.00;
            case "Fries":
                return 5.00;
            case "Soda":
                return 3.00;
            default:
                return 0;
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
        private double price;

        public OrderItem(String item, int quantity, double price) {
            this.item = item;
            this.quantity = quantity;
            this.price = price;
        }

        public String getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getTotalPrice() {
            return price * quantity;
        }

        @Override
        public String toString() {
            return String.format("%s - Quantity: %d - Price: $%.2f", item, quantity, getTotalPrice());
        }
    }
}
