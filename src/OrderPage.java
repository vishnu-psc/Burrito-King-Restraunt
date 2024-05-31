import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import java.util.stream.Collectors;

public class OrderPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;
    private static ObservableList<OrderItem> basket = FXCollections.observableArrayList(); // Static basket
    private ComboBox<String> comboBox;
    private TextField quantityField;
    private Label totalLabel;
    private boolean isVip;

    public OrderPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.isVip = checkVipStatus(username);
        this.view = createView();

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

        ObservableList<String> options = FXCollections.observableArrayList(
                "Burrito", "Fries", "Soda", "Meal (1 Burrito, 1 French Fries, 1 Soda)");
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

        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        grid.add(totalLabel, 1, 3);

        // Displaying basket with + and - buttons
        ListView<OrderItem> basketView = new ListView<>(basket);
        basketView.setCellFactory(param -> new ListCell<OrderItem>() {
            @Override
            protected void updateItem(OrderItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    Button subtractButton = new Button("-");
                    subtractButton
                            .setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
                    subtractButton.setOnAction(e -> {
                        item.decreaseQuantity();
                        if (item.getQuantity() <= 0) {
                            basket.remove(item);
                        }
                        updateTotal();
                        basketView.refresh();
                    });

                    Label itemLabel = new Label(item.toString());
                    Button addButton = new Button("+");
                    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
                    addButton.setOnAction(e -> {
                        item.increaseQuantity();
                        updateTotal();
                        basketView.refresh();
                    });

                    hBox.getChildren().addAll(subtractButton, itemLabel, addButton);
                    setGraphic(hBox);
                }
            }
        });
        basketView.setPrefHeight(200);
        basketView.setStyle("-fx-pref-width: 700px; -fx-font-size: 14px;");
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

        updateTotal();

        return grid;
    }

    private void handleAddToBasket() {
        String item = comboBox.getValue();
        String quantityText = quantityField.getText();
        if (item != null && !quantityText.isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityText);
                double price = getItemPrice(item);
                int waitTime = getItemWaitTime(item);
                basket.add(new OrderItem(item, quantity, price, waitTime, isVip));
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
            int totalWaitTime = basket.stream().mapToInt(OrderItem::getTotalWaitTime).sum();
            double totalAmount = basket.stream().mapToDouble(OrderItem::getTotalPrice).sum();
            String orderDetails = basket.stream()
                    .map(orderItem -> String.format("%s x%d", orderItem.getItem(), orderItem.getQuantity()))
                    .collect(Collectors.joining(", "));

            int orderId = saveOrderToDatabase(orderDetails, totalWaitTime, totalAmount);

            if (orderId != -1) {
                OrderData.orders.addAll(basket);
                comboBox.getSelectionModel().clearSelection();
                quantityField.clear();
                basket.clear();
                updateTotal();
                showAlert("Order Placed",
                        String.format("Your order (ID: %d) has been placed successfully. Preparing time: %d minutes.",
                                orderId, totalWaitTime));
            } else {
                showAlert("Order Failed", "There was an issue placing your order. Please try again.");
            }
        } else {
            showAlert("Empty Basket", "Your basket is empty. Please add items to the basket.");
        }
    }

    private int saveOrderToDatabase(String orderDetails, int totalWaitTime, double totalAmount) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "INSERT INTO orders (orderDetails, status, user, total, dates, waitTime) VALUES (?, 'Placed', ?, ?, ?, ?)";
        int orderId = -1;
        // current dateTime is pushrd in formate of dateTime dataType
        LocalDateTime currentDateTime = LocalDateTime.now();

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, orderDetails);
            pstmt.setString(2, username);
            pstmt.setDouble(3, totalAmount);
            pstmt.setTimestamp(4, Timestamp.valueOf(currentDateTime)); // Set dateTime field to null for now
            pstmt.setInt(5, totalWaitTime);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    private void updateTotal() {
        double total = 0;
        for (OrderItem item : basket) {
            total += item.getTotalPrice();
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    // Price of everything and also in case of vip user meal amount reduced to 18
    private double getItemPrice(String item) {
        switch (item) {
            case "Burrito":
                return 15.00;
            case "Fries":
                return 5.00;
            case "Soda":
                return 3.00;
            case "Meal (1 Burrito, 1 French Fries, 1 Soda)":
                return isVip ? 18.00 : 21.00;
            default:
                return 0;
        }
    }

    // Wait time for each item
    private int getItemWaitTime(String item) {
        switch (item) {
            case "Burrito":
                return 7;
            case "Fries":
                return 4;
            case "Soda":
                return 1;
            case "Meal (1 Burrito, 1 French Fries, 1 Soda)":
                return 10;
            default:
                return 0;
        }
    }

    // Checking user is vip or not after fetching from database
    private boolean checkVipStatus(String username) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT isVip FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("isVip");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
        private int waitTime;
        private boolean isVip;

        public OrderItem(String item, int quantity, double price, int waitTime, boolean isVip) {
            this.item = item;
            this.quantity = quantity;
            this.price = price;
            this.waitTime = waitTime;
            this.isVip = isVip;
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

        public int getWaitTime() {
            return waitTime;
        }

        public int getTotalWaitTime() {
            return waitTime * quantity;
        }

        public double getTotalPrice() {
            return price * quantity;
        }

        public void increaseQuantity() {
            this.quantity++;
        }

        public void decreaseQuantity() {
            this.quantity--;
        }

        @Override
        public String toString() {
            String itemString = String.format("%s - Quantity: %d - Price: $%.2f - Wait Time: %d min", item, quantity,
                    getTotalPrice(), getTotalWaitTime());
            if (isVip && item.equals("Meal (1 Burrito, 1 French Fries, 1 Soda)")) {
                itemString += " (VIP Discount Applied)";
            }
            return itemString;
        }
    }
}
