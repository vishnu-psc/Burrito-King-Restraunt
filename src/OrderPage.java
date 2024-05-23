import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class OrderPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;

    public OrderPage(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.view = createView();
    }

    public GridPane getView() {
        return view;
    }

    private GridPane createView() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Label itemLabel = new Label("Select Item:");
        grid.add(itemLabel, 0, 0);

        ObservableList<String> options = FXCollections.observableArrayList("Burrito", "Fries", "Soda");
        ComboBox<String> comboBox = new ComboBox<>(options);
        grid.add(comboBox, 1, 0);

        Label quantityLabel = new Label("Quantity:");
        grid.add(quantityLabel, 0, 1);

        TextField quantityField = new TextField();
        grid.add(quantityField, 1, 1);

        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setOnAction(e -> handleAddToBasket(comboBox.getValue(), quantityField.getText()));
        grid.add(addToBasketButton, 1, 2);

        Button checkoutButton = new Button("Checkout");
        checkoutButton.setOnAction(e -> handleCheckout());
        grid.add(checkoutButton, 1, 3);

        Button backButton = new Button("Back");
        backButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView())));
        grid.add(backButton, 1, 4);

        return grid;
    }

    private void handleAddToBasket(String item, String quantity) {
        // Add item to the basket logic here
    }

    private void handleCheckout() {
        // Proceed to payment and order placement logic here
    }
}
