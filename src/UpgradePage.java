import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpgradePage {
    private Stage primaryStage;
    private String username;
    private BorderPane view;

    public UpgradePage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.view = createView();
    }

    public BorderPane getView() {
        return view;
    }

    private BorderPane createView() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #f5f5f5;");
        borderPane.setPadding(new Insets(20));

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Upgrade to VIP");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label messageLabel = new Label("For upgrading your account to VIP you need to provide us with your email.");
        messageLabel.setStyle("-fx-font-size: 16px;");

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
        okButton.setOnAction(e -> showEmailInput(borderPane));

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        });

        HBox buttonBox = new HBox(10, okButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(titleLabel, messageLabel, buttonBox);
        borderPane.setCenter(vbox);

        return borderPane;
    }

    private void showEmailInput(BorderPane borderPane) {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Enter your email:");
        emailLabel.setStyle("-fx-font-size: 16px;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(200); // Set the maximum width of the email field

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(e -> handleEmailSubmission(emailField.getText()));

        vbox.getChildren().addAll(emailLabel, emailField, submitButton);
        borderPane.setCenter(vbox);
    }

    private void handleEmailSubmission(String email) {
        if (email == null || email.isEmpty()) {
            showAlert("Error", "Email cannot be empty!");
            return;
        }

        // Here we update the database
        boolean success = updateVipStatus(username, email);
        if (success) {
            // After updating the database, navigate back to the dashboard
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        } else {
            showAlert("Error", "Failed to update VIP status. Please try again.");
        }
    }

    private boolean updateVipStatus(String username, String email) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB"; // Update this to your actual database connection
                                                                  // string

        String sql = "UPDATE Users SET email = ?, isVip = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, "root", "root");
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setBoolean(2, true);
            pstmt.setString(3, username);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
