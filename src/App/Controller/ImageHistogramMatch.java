package App.Controller;

import App.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageHistogramMatch {
    @FXML
    private ImageView imgHistMatch;

    private Stage stage = new Stage();
    public void showImage(Image img) {
        imgHistMatch.setImage(img);
    }

    public void save() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.setInitialFileName("image");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File saveFile = fileChooser.showSaveDialog(stage);
        String path = saveFile.getAbsolutePath();
        String extension = path.substring(path.length()-3);
        // Se a extensão for jpg, ignorar o alpha
        BufferedImage saveImage = null;
        if(imgHistMatch.getImage() == null)
            imgHistMatch.setImage(imgHistMatch.getImage());
        if(extension.equals("jpg")){
            saveImage = new BufferedImage((int)imgHistMatch.getImage().getWidth(), (int)imgHistMatch.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        else {
            saveImage = new BufferedImage((int)imgHistMatch.getImage().getWidth(), (int)imgHistMatch.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        if (saveFile != null) {
            BufferedImage saveImageTemp = SwingFXUtils.fromFXImage(imgHistMatch.getImage(),
                    saveImage);

            saveImage.getGraphics().drawImage(saveImageTemp,0, 0, null);

            ImageIO.write((RenderedImage) saveImage,extension , saveFile);
        }

    }

}
