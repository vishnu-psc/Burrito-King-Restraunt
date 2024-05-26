import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderSummaryPage {
    private Stage primaryStage;
    private String username;
    private ObservableList<OrderPage.OrderItem> basket;
    private VBox view;
    private Label totalLabel;

    public OrderSummaryPage(Stage primaryStage, String username, ObservableList<OrderPage.OrderItem> basket) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.basket = basket;
        this.view = createView();
        primaryStage.setTitle("Order Summary");
        primaryStage.setScene(new Scene(view));
        primaryStage.setFullScreen(true);
        updateTotal();
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

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            DashboardPage dashboardPage = new DashboardPage(primaryStage, username);
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(new Scene(dashboardPage.getView()));
            primaryStage.setFullScreen(true);
        });
        vbox.getChildren().add(backButton);

        return vbox;
    }

    private void updateTotal() {
        double total = 0;
        for (OrderPage.OrderItem item : basket) {
            total += item.getTotalPrice();
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
}
