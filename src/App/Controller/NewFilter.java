package App.Controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class NewFilter {
    @FXML
    private Spinner spn00;
    @FXML
    private Spinner spn01;
    @FXML
    private Spinner spn02;

    @FXML
    private Spinner spn10;
    @FXML
    private Spinner spn11;
    @FXML
    private Spinner spn12;

    @FXML
    private Spinner spn20;
    @FXML
    private Spinner spn21;
    @FXML
    private Spinner spn22;


    public void convolve() throws IOException {
        double[][] kernel = {
                {(double) spn00.getValueFactory().getValue(), (double) spn01.getValueFactory().getValue(), (double) spn02.getValueFactory().getValue()},
                {(double) spn10.getValueFactory().getValue(), (double) spn11.getValueFactory().getValue(), (double) spn12.getValueFactory().getValue()},
                {(double) spn20.getValueFactory().getValue(), (double) spn21.getValueFactory().getValue(), (double) spn22.getValueFactory().getValue()}
        };
        Kernel.kernel = kernel;

    }
}

