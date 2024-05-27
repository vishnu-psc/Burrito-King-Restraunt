import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        backButton.setOnAction(e -> {
            primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView()));
            primaryStage.setFullScreen(true);
            primaryStage.setTitle("Dashboard");
        });
        grid.add(backButton, 1, 4);

        VBox root = new VBox(grid);
        VBox.setVgrow(grid, Priority.ALWAYS);

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Edit Profile");

        return grid;
    }

    // It updates first and lastname also password in database
    private void handleSave(String firstName, String lastName, String password) {
        // Validate input if necessary
        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "All fields are required.");
            return;
        }

        boolean updateSuccess = updateUserProfile(username, firstName, lastName, password);

        if (updateSuccess) {
            showAlert(AlertType.INFORMATION, "Success", "Profile updated successfully.");
        } else {
            showAlert(AlertType.ERROR, "Update Failed", "Profile update failed. Please try again later.");
        }

        primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView()));
        primaryStage.setFullScreen(true); // Ensure full screen mode is set here
        primaryStage.setTitle("Dashboard");
    }

    private boolean updateUserProfile(String username, String firstName, String lastName, String password) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";
        String query = "UPDATE users SET firstname = ?, lastname = ?, password = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement pstmt = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, password);
            pstmt.setString(4, username);

            int affectedRows = pstmt.executeUpdate();

            connection.commit();

            if (affectedRows > 0) {
                System.out.println("Profile updated successfully for user: " + username);
                return true;
            } else {
                System.out.println("No rows affected. Profile update failed for user: " + username);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL error occurred during profile update for user: " + username);
            return false;
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
