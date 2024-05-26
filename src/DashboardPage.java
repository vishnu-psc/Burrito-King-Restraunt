import javafx.collections.FXCollections;
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

public class DashboardPage {
        private Stage primaryStage;
        private String username;
        private BorderPane view;

        public DashboardPage(Stage primaryStage, String username) {
                this.primaryStage = primaryStage;
                this.username = username;
                FXCollections.observableArrayList();
                this.view = createView();
        }

        public BorderPane getView() {
                return view;
        }

        private BorderPane createView() {
                BorderPane borderPane = new BorderPane();
                borderPane.setStyle("-fx-background-color: #f5f5f5;");

                // Top section with Welcome label and Log Out button
                HBox topBox = new HBox();
                topBox.setAlignment(Pos.CENTER);
                topBox.setPadding(new Insets(10, 20, 10, 20));
                topBox.setStyle("-fx-background-color: #333333;");

                // Create a left-aligned box for the welcome label
                HBox leftBox = new HBox();
                leftBox.setAlignment(Pos.CENTER_LEFT);

                Label welcomeLabel = new Label("Welcome, " + username);
                welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
                welcomeLabel.setAlignment(Pos.CENTER_LEFT);

                leftBox.getChildren().add(welcomeLabel);
                HBox.setHgrow(leftBox, javafx.scene.layout.Priority.ALWAYS);

                // Create a right-aligned box for the buttons
                HBox rightBox = new HBox();
                rightBox.setAlignment(Pos.CENTER_RIGHT);

                // Check if user is VIP
                boolean isVip = checkVipStatus(username);

                // Conditionally add the Upgrade button if the user is not VIP
                if (!isVip) {
                        // Upgrade button with crown emoji
                        Button upgradeButton = new Button("👑 Upgrade");
                        upgradeButton.setStyle(
                                        "-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-size: 14px;");
                        upgradeButton.setOnAction(
                                        e -> {
                                                UpgradePage upgradePage = new UpgradePage(primaryStage, username);
                                                primaryStage.setScene(new Scene(upgradePage.getView(),
                                                                primaryStage.getWidth(), primaryStage.getHeight()));
                                                primaryStage.setFullScreen(true);
                                        });

                        rightBox.getChildren().add(upgradeButton);
                        HBox.setMargin(upgradeButton, new Insets(0, 10, 0, 0));
                }

                // Log Out button
                Button logOutButton = new Button("Log Out");
                logOutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
                logOutButton.setOnAction(e -> {
                        LoginPage loginPage = new LoginPage(primaryStage);
                        primaryStage.setScene(new Scene(loginPage.getView()));
                        primaryStage.setFullScreen(true); // Ensure full screen mode is set here
                });

                rightBox.getChildren().add(logOutButton);
                HBox.setMargin(logOutButton, new Insets(0, 0, 0, 10));

                topBox.getChildren().addAll(leftBox, rightBox);

                borderPane.setTop(topBox);

                // Main content area with buttons
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 20, 20, 20));

                Button editProfileButton = new Button("Edit Profile");
                editProfileButton.setStyle(
                                "-fx-pref-width: 200px; -fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
                editProfileButton.setOnAction(
                                e -> primaryStage.setScene(
                                                new Scene(new EditProfilePage(primaryStage, username).getView())));
                grid.add(editProfileButton, 0, 0);

                Button orderButton = new Button("Place Order");
                orderButton.setStyle(
                                "-fx-pref-width: 200px; -fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
                orderButton.setOnAction(
                                e -> primaryStage.setScene(new Scene(new OrderPage(primaryStage, username).getView())));
                grid.add(orderButton, 0, 1);

                Button viewOrdersButton = new Button("View Orders");
                viewOrdersButton.setStyle(
                                "-fx-pref-width: 200px; -fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
                viewOrdersButton.setOnAction(
                                e -> primaryStage.setScene(new Scene(
                                                new OrderSummaryPage(primaryStage, username).getView()))); // Pass
                // the
                // basket
                // here
                grid.add(viewOrdersButton, 0, 2);

                Button exportOrdersButton = new Button("Export Orders");
                exportOrdersButton.setStyle(
                                "-fx-pref-width: 200px; -fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
                exportOrdersButton.setOnAction(e -> {
                        ExportPage exportPage = new ExportPage(primaryStage, username);
                        Scene exportScene = new Scene(exportPage.getView());
                        primaryStage.setScene(exportScene);
                        primaryStage.setFullScreen(true); // Ensure full screen mode is set here
                });
                grid.add(exportOrdersButton, 0, 3);

                borderPane.setCenter(grid);

                return borderPane;
        }

        private boolean checkVipStatus(String username) {
                String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
                String dbUsername = "root"; // Update with your database username
                String dbPassword = "root"; // Update with your database password
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
}
