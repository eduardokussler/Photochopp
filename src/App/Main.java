package App;

import javafx.application.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import App.Controller.WindowController;


public class Main extends Application {
    public static Stage stage = new Stage();


    @Override
    public void start(Stage primaryStage) throws Exception {

        WindowController.showView();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
