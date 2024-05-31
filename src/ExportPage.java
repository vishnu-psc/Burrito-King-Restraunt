import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;
    private double totalAmount;
    private int availableCredits;
    private Label totalLabel;
    private CheckBox useCreditsCheckBox;
    private boolean isVip;

    public ExportPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.totalAmount = calculateTotalAmount();
        this.availableCredits = fetchAvailableCredits();
        this.isVip = fetchIsVip();
        this.view = createView();
        primaryStage.setTitle("Pay");
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
        grid.setStyle("-fx-padding: 20px; -fx-background-color: #f0f0f0;");

        Label exportLabel = new Label("Export Orders for " + username);
        exportLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        grid.add(exportLabel, 0, 0, 2, 1);

        // View Order History button
        Button viewOrderHistoryButton = new Button("View Order History");
        viewOrderHistoryButton.setStyle(
                "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        viewOrderHistoryButton.setOnAction(e -> generateOrderHistoryCSV());
        grid.add(viewOrderHistoryButton, 1, 0);
        GridPane.setHalignment(viewOrderHistoryButton, HPos.RIGHT);

        // Total amount label
        totalLabel = new Label("Total Amount: $" + totalAmount);
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(totalLabel, 0, 1, 2, 1);

        // Card details fields
        Label cardNumberLabel = new Label("Card Number:");
        cardNumberLabel.setStyle("-fx-font-size: 14px;");
        grid.add(cardNumberLabel, 0, 2);
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cardNumberField.setStyle("-fx-pref-width: 200px; -fx-font-size: 14px;");
        grid.add(cardNumberField, 1, 2);

        Label cvvLabel = new Label("CVV:");
        cvvLabel.setStyle("-fx-font-size: 14px;");
        grid.add(cvvLabel, 0, 3);
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.setStyle("-fx-pref-width: 60px; -fx-font-size: 14px;");
        grid.add(cvvField, 1, 3);

        Label expiryDateLabel = new Label("Expiry Date (MM/YYYY):");
        expiryDateLabel.setStyle("-fx-font-size: 14px;");
        grid.add(expiryDateLabel, 0, 4);
        TextField expiryDateField = new TextField();
        expiryDateField.setPromptText("MM/YYYY");
        expiryDateField.setStyle("-fx-pref-width: 100px; -fx-font-size: 14px;");
        grid.add(expiryDateField, 1, 4);

        if (isVip) {
            useCreditsCheckBox = new CheckBox("Use Credit Points");
            useCreditsCheckBox.setStyle("-fx-font-size: 14px;");
            grid.add(useCreditsCheckBox, 0, 5);

            Label creditsLabel = new Label("Available Credits: " + availableCredits);
            creditsLabel.setStyle("-fx-font-size: 14px;");
            grid.add(creditsLabel, 1, 5);

            useCreditsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateTotalAmount());
        }

        Button payButton = new Button("Pay");
        payButton.setStyle(
                "-fx-pref-width: 100px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        payButton.setOnAction(e -> handlePay(cardNumberField.getText(), cvvField.getText(), expiryDateField.getText(),
                isVip && useCreditsCheckBox.isSelected()));
        grid.add(payButton, 1, 6);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 100px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(
                e -> {
                    DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
                    primaryStage.setTitle("Dashboard");
                    primaryStage.setScene(new Scene(dashboardPage.getView()));
                    primaryStage.setFullScreen(true);
                });
        grid.add(backButton, 1, 7);

        return grid;
    }

    private double calculateTotalAmount() {
        return OrderData.orders.stream().mapToDouble(OrderPage.OrderItem::getTotalPrice).sum();
    }

    private int fetchAvailableCredits() {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT points FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("points");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private boolean fetchIsVip() {
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

    private void updateTotalAmount() {
        double discount = 0;
        if (useCreditsCheckBox != null && useCreditsCheckBox.isSelected()) {
            int creditsToUse = Math.min(availableCredits, (int) (totalAmount * 100));
            discount = creditsToUse / 100.0;
        }
        totalLabel.setText("Total Amount: $" + String.format("%.2f", (totalAmount - discount)));
    }

    private void updateLastOrderStatusToActive() {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "UPDATE orders SET status = 'Active' WHERE user = ? ORDER BY orderID DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handlePay(String cardNumber, String cvv, String expiryDate, boolean useCredits) {
        if (validateCardDetails(cardNumber, cvv, expiryDate)) {
            double discount = 0;
            int newCreditsBalance = availableCredits;
            if (useCredits) {
                int creditsToUse = Math.min(availableCredits, (int) (totalAmount * 100));
                discount = creditsToUse / 100.0;
                totalAmount -= discount;
                newCreditsBalance -= creditsToUse;
            }

            showAlert("Payment Successful",
                    "Your payment has been processed successfully. Total amount paid: $"
                            + String.format("%.2f", totalAmount));

            updateLastOrderStatusToActive();

            OrderData.orders.clear();

            if (isVip) {
                int creditsEarned = (int) totalAmount;
                addCredits(creditsEarned);
                updateCredits(newCreditsBalance + creditsEarned);
            } else {
                updateCredits(newCreditsBalance);
            }

            // Show alert for collection time
            int waitingTime = fetchWaitingTime();
            String collectionTime = calculateCollectionTime(waitingTime);
            showAlert("Order Collection Information",
                    "You can collect your order after " + collectionTime + ".");

            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        } else {
            showAlert("Invalid Card Details", "Please enter valid card details.");
        }
    }

    private boolean validateCardDetails(String cardNumber, String cvv, String expiryDate) {
        return cardNumber.matches("\\d{16}") && cvv.matches("\\d{3}") && expiryDate.matches("(0[1-9]|1[0-2])/\\d{4}");
    }

    // Updates points in database
    private void updateCredits(int newCredits) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "UPDATE users SET points = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, newCredits);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCredits(int credits) {
        updateCredits(availableCredits + credits);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // To generate csv file
    private void generateOrderHistoryCSV() {
        List<String[]> orderHistory = fetchOrderHistory();

        try (FileWriter csvWriter = new FileWriter("OrderHistory_" + username + ".csv")) {
            csvWriter.append("Order ID,Order Details,Status,Total\n");
            for (String[] order : orderHistory) {
                csvWriter.append(String.join(",", order));
                csvWriter.append("\n");
            }
            csvWriter.flush();

            showAlert("Order History Exported", "Order history has been exported to CSV file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while exporting order history.");
        }
    }

    // to fetch history from database
    private List<String[]> fetchOrderHistory() {
        List<String[]> orderHistory = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT orderID, orderDetails, status, total FROM orders WHERE user = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] order = {
                        rs.getString("orderID"),
                        rs.getString("orderDetails"),
                        rs.getString("status"),
                        String.format("%.2f", rs.getDouble("total"))
                };
                orderHistory.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderHistory;
    }

    // the waiting time from the database
    private int fetchWaitingTime() {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT waitTime FROM orders WHERE user = ? AND status = 'Active' ORDER BY waitTime DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("waitTime");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // adding wait time to current time
    private String calculateCollectionTime(int waitingTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime collectionTime = currentTime.plusMinutes(waitingTime);

        return collectionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
