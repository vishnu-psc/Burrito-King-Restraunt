import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditProfilePage {
    private Stage primaryStage;
    private String username;
    private GridPane view;

    public EditProfilePage(Stage primaryStage, String username) {
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
        grid.setPadding(new Insets(20));

        Label firstNameLabel = new Label("First Name:");
        grid.add(firstNameLabel, 0, 0);

        TextField firstNameField = new TextField();
        grid.add(firstNameField, 1, 0);

        Label lastNameLabel = new Label("Last Name:");
        grid.add(lastNameLabel, 0, 1);

        TextField lastNameField = new TextField();
        grid.add(lastNameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-pref-width: 100px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(
                e -> handleSave(firstNameField.getText(), lastNameField.getText(), passwordField.getText()));
        grid.add(saveButton, 1, 3);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-pref-width: 100px; -fx-background-color: #f44336; -fx-text-fill: white;");
        backButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView())));
        grid.add(backButton, 1, 4);

        // Make sure the grid stretches to fill the window
        VBox root = new VBox(grid);
        VBox.setVgrow(grid, Priority.ALWAYS);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);

        return grid;
    }

    private void handleSave(String firstName, String lastName, String password) {
        // Handle profile update logic here
        primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView()));
    }
}