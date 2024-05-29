import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderSummaryPage {
    private Stage primaryStage;
    private String username;
    private VBox view;
    private Label totalLabel;

    public OrderSummaryPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.view = createView();
        primaryStage.setTitle("Order Summary");
        primaryStage.setScene(new Scene(view));
        primaryStage.setFullScreen(true);
        updateTotal();

        // Add a listener to the orders list to update the total when the list changes
        OrderData.orders
                .addListener((javafx.collections.ListChangeListener<OrderPage.OrderItem>) change -> updateTotal());
    }

    public VBox getView() {
        return view;
    }

    private VBox createView() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Order Summary");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333333;");
        vbox.getChildren().add(titleLabel);

        ListView<OrderPage.OrderItem> orderSummaryView = new ListView<>(OrderData.orders);
        orderSummaryView.setPrefHeight(200);
        orderSummaryView.setStyle("-fx-pref-width: 400px; -fx-font-size: 14px;");
        vbox.getChildren().add(orderSummaryView);

        totalLabel = new Label("Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
        vbox.getChildren().add(totalLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        });

        Button okButton = new Button("OK");
        okButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        okButton.setOnAction(e -> handleOkButton());

        buttonBox.getChildren().addAll(backButton, okButton);
        vbox.getChildren().add(buttonBox);

        return vbox;
    }

    private void updateTotal() {
        double total = 0;
        for (OrderPage.OrderItem item : OrderData.orders) {
            total += item.getTotalPrice();
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void handleOkButton() {
        double totalAmount = 0;
        for (OrderPage.OrderItem item : OrderData.orders) {
            totalAmount += item.getTotalPrice();
        }

        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "INSERT INTO orders (orderDetails, status, user, total) VALUES (?, 'Active', ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            String orderDetails = OrderData.orders.stream()
                    .map(order -> order.getItem() + " x " + order.getQuantity())
                    .reduce((order1, order2) -> order1 + ", " + order2)
                    .orElse("");

            pstmt.setString(1, orderDetails);
            pstmt.setString(2, username);
            pstmt.setDouble(3, totalAmount);

            pstmt.executeUpdate();
            System.out.println("DONE!!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("NONE!!!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
