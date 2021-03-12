package App.Controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;


public class Histogram {

    public static Stage stage = new Stage();

    @FXML
    private ImageView histogram;

    public void showHistogram(Image img) {
        histogram.setImage(img);
    }

    public static void histCalc(int[] hist, BufferedImage image) {
        int pixel;
        int blueMask = 0x000000ff;
        for (int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = image.getRGB(i,j);
                // Pegando o valor do tom
                pixel = pixel & blueMask;
                hist[pixel] = hist[pixel] + 1;
            }
        }

    }

    public static int[] calcHistCum(int [] hist, BufferedImage image) {
        int[] hist_cum = new int[256];
        hist_cum[0] = (int) (hist[0]);
        for (int i = 1; i < hist.length; i++) {
            hist_cum[i] = (int) (hist_cum[i - 1] + hist[i]);
        }
        for (int i = 1; i < hist.length; i++) {
            hist_cum[i] = Math.round((255 * hist_cum[i]) / (image.getHeight() * image.getWidth()));
        }

        return hist_cum;
    }
}
