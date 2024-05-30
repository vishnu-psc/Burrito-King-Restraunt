import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
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

    public OrderSummaryPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.view = createView();
        primaryStage.setTitle("Order Summary");
        primaryStage.setScene(new Scene(view));
        primaryStage.setFullScreen(true);
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

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button collectedButton = new Button("Collected");
        collectedButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 14px;");
        collectedButton.setOnAction(e -> showOrders("Collected"));

        Button canceledButton = new Button("Canceled");
        canceledButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        canceledButton.setOnAction(e -> showOrders("Canceled"));

        buttonBox.getChildren().addAll(collectedButton, canceledButton);
        vbox.getChildren().add(buttonBox);

        // Table to show orders
        TableView<OrderItem> ordersTable = new TableView<>();
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OrderItem, String> orderIdColumn = new TableColumn<>("Order ID");
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<OrderItem, String> orderDetailsColumn = new TableColumn<>("Order Details");
        orderDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("orderDetails"));

        ordersTable.getColumns().addAll(orderIdColumn, orderDetailsColumn);
        vbox.getChildren().add(ordersTable);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #2196f3; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        });

        HBox backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.CENTER);
        backButtonBox.setPadding(new Insets(10, 0, 0, 0));
        vbox.getChildren().add(backButtonBox);

        return vbox;
    }

    private void showOrders(String status) {
        ObservableList<OrderItem> orders = FXCollections.observableArrayList();
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "SELECT orderId, orderDetails FROM orders WHERE user = ? AND status = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String orderId = rs.getString("orderId");
                String orderDetails = rs.getString("orderDetails");
                orders.add(new OrderItem(orderId, orderDetails));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableView<OrderItem> ordersTable = (TableView<OrderItem>) view.getChildren().get(2);
        ordersTable.setItems(orders);
    }

    public static class OrderItem {
        private final String orderId;
        private final String orderDetails;

        public OrderItem(String orderId, String orderDetails) {
            this.orderId = orderId;
            this.orderDetails = orderDetails;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getOrderDetails() {
            return orderDetails;
        }
    }
}
