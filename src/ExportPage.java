import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ExportPage {
    private Stage primaryStage;
    private String username;
    private GridPane view;

    public ExportPage(Stage primaryStage, String username) {
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

        Label exportLabel = new Label("Export Orders for " + username);
        grid.add(exportLabel, 0, 0);

        CheckBox selectItemsCheckBox = new CheckBox("Include Ordered Items");
        grid.add(selectItemsCheckBox, 0, 1);

        CheckBox selectPriceCheckBox = new CheckBox("Include Total Price");
        grid.add(selectPriceCheckBox, 0, 2);

        Label fileNameLabel = new Label("File Name:");
        grid.add(fileNameLabel, 0, 3);

        TextField fileNameField = new TextField();
        grid.add(fileNameField, 1, 3);

        Button exportButton = new Button("Export");
        exportButton.setOnAction(e -> handleExport(selectItemsCheckBox.isSelected(), selectPriceCheckBox.isSelected(),
                fileNameField.getText()));
        grid.add(exportButton, 1, 4);

        Button backButton = new Button("Back");
        backButton.setOnAction(
                e -> primaryStage.setScene(new Scene(new DashboardPage(primaryStage, username).getView())));
        grid.add(backButton, 1, 5);

        return grid;
    }

    private void handleExport(boolean includeItems, boolean includePrice, String fileName) {
        // Handle export logic here
    }
}
