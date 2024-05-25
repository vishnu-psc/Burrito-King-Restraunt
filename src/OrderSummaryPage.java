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

    public OrderSummaryPage(Stage primaryStage, String username, ObservableList<OrderPage.OrderItem> basket) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.basket = basket;
        this.view = createView();
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

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-pref-width: 150px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView(), 800, 600)));
        vbox.getChildren().add(backButton);

        return vbox;
    }
}