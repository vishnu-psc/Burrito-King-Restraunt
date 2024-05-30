import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;
    private double totalAmount;
    private int availableCredits;
    private Label totalLabel;
    private CheckBox useCreditsCheckBox;

    public ExportPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.totalAmount = calculateTotalAmount();
        this.availableCredits = fetchAvailableCredits();
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

        // Credit points section
        useCreditsCheckBox = new CheckBox("Use Credit Points");
        useCreditsCheckBox.setStyle("-fx-font-size: 14px;");
        grid.add(useCreditsCheckBox, 0, 5);

        Label creditsLabel = new Label("Available Credits: " + availableCredits);
        creditsLabel.setStyle("-fx-font-size: 14px;");
        grid.add(creditsLabel, 1, 5);

        // Add listener to update total amount when checkbox is toggled
        useCreditsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateTotalAmount());

        Button payButton = new Button("Pay");
        payButton.setStyle(
                "-fx-pref-width: 100px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        payButton.setOnAction(e -> handlePay(cardNumberField.getText(), cvvField.getText(), expiryDateField.getText(),
                useCreditsCheckBox.isSelected()));
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

    private void updateTotalAmount() {
        double discount = 0;
        if (useCreditsCheckBox.isSelected()) {
            int creditsToUse = Math.min(availableCredits, (int) (totalAmount * 100));
            discount = creditsToUse / 100.0;
        }
        totalLabel.setText("Total Amount: $" + String.format("%.2f", (totalAmount - discount)));
    }

    private void handlePay(String cardNumber, String cvv, String expiryDate, boolean useCredits) {
        if (validateCardDetails(cardNumber, cvv, expiryDate)) {
            TextInputDialog dateDialog = new TextInputDialog();
            dateDialog.setTitle("Enter Current Date");
            dateDialog.setHeaderText("Please enter the current date in YYYY-MM-DD format:");
            dateDialog.setContentText("Date:");

            TextInputDialog timeDialog = new TextInputDialog();
            timeDialog.setTitle("Enter Current Time");
            timeDialog.setHeaderText("Please enter the current time in HH:MM format:");
            timeDialog.setContentText("Time:");

            String currentDate = dateDialog.showAndWait().orElse("");
            String currentTime = timeDialog.showAndWait().orElse("");

            if (!currentDate.isEmpty() && !currentTime.isEmpty() && validateDateTime(currentDate, currentTime)) {
                String dateTime = currentDate + " " + currentTime + ":00";

                double discount = 0;
                int newCreditsBalance = availableCredits;
                if (useCredits) {
                    int creditsToUse = Math.min(availableCredits, (int) (totalAmount * 100));
                    discount = creditsToUse / 100.0;
                    totalAmount -= discount;
                    newCreditsBalance -= creditsToUse;
                }

                // Insert order details into the orders table with the current date and time
                insertOrderDetails(dateTime);

                showAlert("Payment Successful",
                        "Your payment has been processed successfully. Total amount paid: $"
                                + String.format("%.2f", totalAmount));

                // Clear orders and add credits
                OrderData.orders.clear();
                int creditsEarned = (int) totalAmount;
                addCredits(creditsEarned);

                updateCredits(newCreditsBalance + creditsEarned);

                // Show alert for collection time
                int waitingTime = fetchWaitingTime();
                String collectionTime = calculateCollectionTime(currentDate, currentTime, waitingTime);
                showAlert("Order Collection Information",
                        "You can collect your order after " + collectionTime + ".");

                DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
                primaryStage.setTitle("Dashboard");
                primaryStage.setScene(new Scene(dashboardPage.getView()));
                primaryStage.setFullScreen(true);
            } else {
                showAlert("Invalid Date/Time", "Please enter valid date and time in the specified formats.");
            }
        } else {
            showAlert("Invalid Card Details", "Please enter valid card details.");
        }
    }

    private boolean validateCardDetails(String cardNumber, String cvv, String expiryDate) {
        return cardNumber.matches("\\d{16}") && cvv.matches("\\d{3}") && expiryDate.matches("(0[1-9]|1[0-2])/\\d{4}");
    }

    private boolean validateDateTime(String date, String time) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}") && time.matches("\\d{2}:\\d{2}");
    }

    public void insertOrderDetails(String dateTime) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "INSERT INTO orders (orderDetails, status, user, total, dates, waitTime) VALUES (?, 'Active', ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            String orderDetails = OrderData.orders.stream()
                    .map(order -> order.getItem() + " x " + order.getQuantity())
                    .collect(Collectors.joining(", "));

            int waitTime = OrderData.orders.stream()
                    .mapToInt(OrderPage.OrderItem::getWaitTime)
                    .sum();

            pstmt.setString(1, orderDetails);
            pstmt.setString(2, username);
            pstmt.setDouble(3, totalAmount);
            pstmt.setString(4, dateTime);
            pstmt.setInt(5, waitTime);

            pstmt.executeUpdate();
            System.out.println("DONE!!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("NONE!!!");
        }
    }

    // Updates points(also vip credits) in database
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

    // Method to generate a CSV file with order history
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

    // Method to fetch order history from the database
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

    // Fetch the waiting time from the database
    private int fetchWaitingTime() {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT waitTime FROM orders WHERE user = ? AND status = 'Active'"; // Assuming there's only
                                                                                           // one row with id = 1
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("waitTime");

            }
            System.out.println("YES");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("NO");
        }

        return 30; // Default waiting time if not found in the database
    }

    // Calculate collection time by adding waiting time to the current time
    private String calculateCollectionTime(String currentDate, String currentTime, int waitingTime) {
        String[] dateParts = currentDate.split("-");
        String[] timeParts = currentTime.split(":");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Adding the fetched waiting time for order preparation
        minute += waitingTime;
        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            hour %= 24;
            day += 1;
            // Simplified approach for incrementing the day without considering month-end
            // adjustments (this can be refined further for real applications)
        }

        return String.format("%04d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);
    }
}
