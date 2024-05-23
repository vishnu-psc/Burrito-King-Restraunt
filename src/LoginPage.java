import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class LoginPage {
    private Stage primaryStage;
    private BorderPane view;

    public LoginPage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.view = createView();
    }

    public BorderPane getView() {
        return view;
    }

    private BorderPane createView() {
        BorderPane borderPane = new BorderPane();

        // Title
        Label titleLabel = new Label("Welcome to Burrito King");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 20, 0));
        borderPane.setTop(titleBox);

        // Login form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(150);
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        HBox loginBtnBox = new HBox(10);
        loginBtnBox.setAlignment(Pos.BOTTOM_RIGHT);
        loginBtnBox.getChildren().add(loginButton);
        grid.add(loginBtnBox, 1, 4);

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(150);
        registerButton.setOnAction(e -> handleRegister(usernameField.getText(), passwordField.getText()));
        HBox registerBtnBox = new HBox(10);
        registerBtnBox.setAlignment(Pos.BOTTOM_LEFT);
        registerBtnBox.getChildren().add(registerButton);
        grid.add(registerBtnBox, 0, 4);

        borderPane.setCenter(grid);

        return borderPane;
    }

    private void handleLogin(String username, String password) {
        // Handle login logic here, then transition to DashboardPage
        primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView(), 800, 600));
        primaryStage.setFullScreen(true);
    }

    private void handleRegister(String username, String password) {
        // Handle registration logic here, then transition to DashboardPage
        primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView(), 800, 600));
        primaryStage.setFullScreen(true);
    }
}
