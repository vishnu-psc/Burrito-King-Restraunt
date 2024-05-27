import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//This file is used for collecting information of order from ordeer page and link to other pages 
// without using database for it
public class OrderData {
    public static ObservableList<OrderPage.OrderItem> orders = FXCollections.observableArrayList();
}