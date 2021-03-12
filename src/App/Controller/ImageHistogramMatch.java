package App.Controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageHistogramMatch {
    @FXML
    private ImageView imgHistMatch;

    public void showImage(Image img) {
        imgHistMatch.setImage(img);
    }

}
