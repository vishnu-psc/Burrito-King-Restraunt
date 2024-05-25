import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DashboardPage {
        private Stage primaryStage;
        private String username;
        private BorderPane view;
        private ObservableList<OrderPage.OrderItem> basket;

        public DashboardPage(Stage primaryStage, String username) {
                this.primaryStage = primaryStage;
                this.username = username;
                this.basket = FXCollections.observableArrayList(); // Initialize the basket here
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

                // Create a right-aligned box for the logout button
                HBox rightBox = new HBox();
                rightBox.setAlignment(Pos.CENTER_RIGHT);

                Button logOutButton = new Button("Log Out");
                logOutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
                logOutButton.setOnAction(e -> primaryStage.setScene(new Scene(new LoginPage(primaryStage).getView())));

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
                                                new OrderSummaryPage(primaryStage, username, basket).getView()))); // Pass
                                                                                                                   // the
                                                                                                                   // basket
                                                                                                                   // here
                grid.add(viewOrdersButton, 0, 2);

                Button exportOrdersButton = new Button("Export Orders");
                exportOrdersButton.setStyle(
                                "-fx-pref-width: 200px; -fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
                exportOrdersButton.setOnAction(
                                e -> primaryStage
                                                .setScene(new Scene(new ExportPage(primaryStage, username).getView())));
                grid.add(exportOrdersButton, 0, 3);

                borderPane.setCenter(grid);

                return borderPane;
        }
}