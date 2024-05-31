import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AwaitedOrdersPage {
    private Stage primaryStage;
    private String username;
    private BorderPane view;

    public AwaitedOrdersPage(Stage primaryStage, String username) {
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

        Label titleLabel = new Label("Awaited Orders for " + username);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        borderPane.setTop(titleLabel);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));

        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT orderId, orderDetails FROM orders WHERE user = ? AND status = 'Active'";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            int row = 0;
            while (rs.next()) {
                String orderId = rs.getString("orderId");
                String orderDetails = rs.getString("orderDetails");

                Label orderLabel = new Label("Order ID: " + orderId + " - " + orderDetails);
                orderLabel.setStyle("-fx-font-size: 16px;");
                grid.add(orderLabel, 0, row);

                Button collectButton = new Button("Collect");
                styleButton(collectButton, "#4caf50");
                collectButton.setOnAction(e -> updateOrderStatus(orderId, "Collected"));
                grid.add(collectButton, 1, row);

                Button cancelButton = new Button("Cancel");
                styleButton(cancelButton, "#f44336");
                cancelButton.setOnAction(e -> updateOrderStatus(orderId, "Canceled"));
                grid.add(cancelButton, 2, row);

                row++;
            }

            if (row == 0) {
                Label noOrdersLabel = new Label("No awaited orders found.");
                noOrdersLabel.setStyle("-fx-font-size: 16px;");
                grid.add(noOrdersLabel, 0, 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        borderPane.setCenter(grid);

        Button backButton = new Button("Back");
        styleButton(backButton, "#2196f3");
        backButton.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            Scene dashboardScene = new Scene(dashboardPage.getView());
            primaryStage.setScene(dashboardScene);
            primaryStage.setFullScreen(true);
        });

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.CENTER);
        backButtonBox.setPadding(new Insets(20));
        borderPane.setBottom(backButtonBox);

        return borderPane;
    }

    // To update status to collected or canceled
    private void updateOrderStatus(String orderId, String status) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "UPDATE orders SET status = ? WHERE orderId = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setString(2, orderId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Refresh the page to reflect changes
                AwaitedOrdersPage awaitedOrdersPage = new AwaitedOrdersPage(primaryStage, username);
                Scene awaitedOrdersScene = new Scene(awaitedOrdersPage.getView());
                primaryStage.setScene(awaitedOrdersScene);
                primaryStage.setFullScreen(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-pref-width: 100px; " +
                        "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-pref-width: 100px; " +
                        "-fx-background-color: #333333; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-pref-width: 100px; " +
                        "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-cursor: hand;"));
    }
}
