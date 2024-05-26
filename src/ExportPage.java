import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ExportPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;

    public ExportPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
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

        // Total amount label
        Label totalLabel = new Label("Total Amount: $" + calculateTotalAmount());
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

        Button payButton = new Button("Pay");
        payButton.setStyle(
                "-fx-pref-width: 100px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        payButton.setOnAction(e -> handlePay(cardNumberField.getText(), cvvField.getText(), expiryDateField.getText()));
        grid.add(payButton, 1, 5);

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
        grid.add(backButton, 1, 6);

        return grid;
    }

    private double calculateTotalAmount() {
        return OrderData.orders.stream().mapToDouble(OrderPage.OrderItem::getTotalPrice).sum();
    }

    private void handlePay(String cardNumber, String cvv, String expiryDate) {
        if (validateCardDetails(cardNumber, cvv, expiryDate)) {
            // Process the payment
            // ...

            // Show confirmation message
            showAlert("Payment Successful", "Your payment has been processed successfully.");

            // Clear orders
            OrderData.orders.clear();

            // Navigate back to the DashboardPage
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
