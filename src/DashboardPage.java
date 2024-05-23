import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DashboardPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;

    public DashboardPage(Stage primaryStage, String username) {
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

        Label welcomeLabel = new Label("Welcome, " + username);
        grid.add(welcomeLabel, 0, 0);

        // Add other components like active orders, edit profile, etc.

        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new EditProfilePage(primaryStage, username).getView())));
        grid.add(editProfileButton, 0, 1);

        Button orderButton = new Button("Place Order");
        orderButton.setOnAction(e -> primaryStage.setScene(new Scene(new OrderPage(primaryStage, username).getView())));
        grid.add(orderButton, 0, 2);

        Button viewOrdersButton = new Button("View Orders");
        viewOrdersButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new OrderSummaryPage(primaryStage, username).getView())));
        grid.add(viewOrdersButton, 0, 3);

        Button exportOrdersButton = new Button("Export Orders");
        exportOrdersButton
                .setOnAction(e -> primaryStage.setScene(new Scene(new ExportPage(primaryStage, username).getView())));
        grid.add(exportOrdersButton, 0, 4);

        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(e -> primaryStage.setScene(new Scene(new LoginPage(primaryStage).getView())));
        grid.add(logOutButton, 0, 5);

        return grid;
    }
}
