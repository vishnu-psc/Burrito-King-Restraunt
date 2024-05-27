import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage {
    private Stage primaryStage;
    private BorderPane view;
    private GridPane formGrid;

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = createView();
    }

    public BorderPane getView() {
        return view;
    }

    private BorderPane createView() {
        BorderPane borderPane = new BorderPane();

        Label titleLabel = new Label("Welcome to Burrito King");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 20, 0));
        borderPane.setTop(titleBox);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 20, 0));

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        loginButton.setOnAction(e -> showLoginForm());

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(150);
        registerButton.setStyle("-fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 14px;");
        registerButton.setOnAction(e -> showRegisterForm());

        buttonBox.getChildren().addAll(loginButton, registerButton);
        borderPane.setCenter(buttonBox);

        formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(25, 25, 25, 25));

        borderPane.setBottom(formGrid);

        return borderPane;
    }

    private void showLoginForm() {
        formGrid.getChildren().clear();

        Label usernameLabel = new Label("Username:");
        formGrid.add(usernameLabel, 0, 0);

        TextField usernameField = new TextField();
        formGrid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        formGrid.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        formGrid.add(passwordField, 1, 1);

        Button submitButton = new Button("Submit");
        submitButton.setPrefWidth(150);
        submitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        formGrid.add(submitButton, 1, 2);
    }

    private void showRegisterForm() {
        formGrid.getChildren().clear();

        Label usernameLabel = new Label("Enter a unique Username:");
        formGrid.add(usernameLabel, 0, 0);

        TextField usernameField = new TextField();
        formGrid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        formGrid.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        formGrid.add(passwordField, 1, 1);

        Button submitButton = new Button("Submit and Register Me");
        submitButton.setPrefWidth(150);
        submitButton.setStyle("-fx-background-color: #2b7087; -fx-text-fill: white; -fx-font-size: 11px;");
        submitButton.setOnAction(
                e -> handleRegister(usernameField.getText(), passwordField.getText()));
        formGrid.add(submitButton, 1, 3);
    }

    private void handleLogin(String username, String password) {
        if (validateLogin(username, password)) {
            primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView(), 800, 600));
            primaryStage.setFullScreen(true);
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private void handleRegister(String username, String password) {
        if (registerUser(username, password)) {
            showAlert("Registration Successful", "You have been registered successfully.");
            showLoginForm();
        } else {
            showAlert("Registration Failed", "Username already exists or error occurred.");
        }
    }

    // Handle login is valid or authenticated
    private boolean validateLogin(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Register new int database
    private boolean registerUser(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/BurritoKingDB";
        String dbUsername = "root";
        String dbPassword = "root";

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
