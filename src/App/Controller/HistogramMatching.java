package App.Controller;

import App.Main;
import App.Controller.Histogram;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class HistogramMatching {
    @FXML
    private ImageView imgFst;

    @FXML
    private ImageView imgSnd;

    public Stage stage = new Stage();

    public void loadImage(ImageView imgView) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getSelectedExtensionFilter());
        File imagem;
        imagem = fileChooser.showOpenDialog(stage);
        BufferedImage image = ImageIO.read(imagem);

        Image showImage = null;
        showImage = SwingFXUtils.toFXImage(image, (WritableImage) showImage);
        double imageHeight = showImage.getHeight();
        double imageWidth = showImage.getWidth();
        //originalImage.setViewport(new Rectangle2D(imageWidth,imageHeight,imageWidth,imageHeight));
        imgView.setImage(showImage);
    }

    public void loadImage1() throws IOException {
        loadImage(imgFst);
    }

    public void loadImage2() throws IOException {
        loadImage(imgSnd);
    }

    private BufferedImage luminance(BufferedImage img) {
        int pixel;
        int alpha = 255;
        int red   = 0;
        int redMask = 0x00ff0000;
        int green = 255;
        int greenMask = 0x0000ff00;
        int blue  = 0;
        int blueMask = 0x000000ff;
        // argb é um inteiro (32bits) que representa o pixel
        // AAAAAAAA|RRRRRRRR|GGGGGGGG|BBBBBBBBB
        int luminance;
        int argb = 0;
        for(int i = 0; i < img.getWidth(); i++){
            for(int j = 0; j < img.getHeight(); j++){
                pixel = img.getRGB(i,j);
                blue = pixel & blueMask;
                blue = (int) (blue * 0.114);
                red = pixel & redMask;
                // Fazendo o shift para o valor do vermelho não ter peso maior
                red =(int) ((red >> 16) * 0.299);
                green = pixel & greenMask;
                // Fazendo o shift para o valor do verde não ter peso maior
                green =(int) ((green >> 8) * 0.587);
                luminance = red + green + blue;
                argb = Pixels.assemblePixel(luminance);
                img.setRGB(i,j, argb);
            }
        }
        return img;
    }

    
    private int findTargetShadeClosest(int shade, int [] srcHistCum, int [] targetHistCum ) {
        int val = srcHistCum[shade];
        int [] diffs = new int[256];
        // Calculando as diferenças entre os histogramas
        for(int i = 0; i < targetHistCum.length; i++) {
            diffs[i] = Math.abs(targetHistCum[i] - val);
        }
        // Encontrando a menor diferença
        int leastDiff = diffs[0];
        int leastIndex = 0;
        for(int i = 1; i < diffs.length; i++) {
            if(diffs[i] <= leastDiff) {
                leastDiff = diffs[i];
                leastIndex = i;
            }
        }
        return leastIndex;
    }


    public void histMatch() throws IOException {
        // Creating another window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageHistogramMatch.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Photochopp - Image Matched");
        stage.setScene(new Scene(root, 1200, 900));
        stage.show();
        ImageHistogramMatch histogramMatching = loader.getController();

        BufferedImage imgMatched =  SwingFXUtils.fromFXImage(imgFst.getImage(),null);
        // Testando se as imagens já estão em preto e branco ou não
        int randPixel = SwingFXUtils.fromFXImage(imgFst.getImage(), null).getRGB((int)(Math.random() * imgFst.getImage().getWidth()), (int)(Math.random() * imgFst.getImage().getHeight()));
        if(Pixels.getR(randPixel) != Pixels.getG(randPixel) || Pixels.getR(randPixel) != Pixels.getB(randPixel) || Pixels.getB(randPixel) != Pixels.getG(randPixel)) {
            BufferedImage image = SwingFXUtils.fromFXImage(imgFst.getImage(), null);
            imgFst.setImage(SwingFXUtils.toFXImage(luminance(image), null));
        }
        randPixel = SwingFXUtils.fromFXImage(imgSnd.getImage(), null).getRGB((int)(Math.random() * imgSnd.getImage().getWidth()), (int)(Math.random() * imgSnd.getImage().getHeight()));
        if(Pixels.getR(randPixel) != Pixels.getG(randPixel) || Pixels.getR(randPixel) != Pixels.getB(randPixel) || Pixels.getB(randPixel) != Pixels.getG(randPixel)) {
            BufferedImage image = SwingFXUtils.fromFXImage(imgSnd.getImage(), null);
            imgSnd.setImage(SwingFXUtils.toFXImage(luminance(image), null));
        }

        // Calculando os histogramas
        int [] histFstImg = new int[256];
        int [] histSndImg = new int[256];
        int [] histFstImgCum = new int[256];
        int [] histSndImgCum = new int[256];
        int [] histogramMatch = new int[256];
        BufferedImage fstImg = SwingFXUtils.fromFXImage(imgFst.getImage(), null);
        BufferedImage sndImg = SwingFXUtils.fromFXImage(imgSnd.getImage(), null);
        Histogram.histCalc(histFstImg, fstImg);
        Histogram.histCalc(histSndImg, sndImg);
        histFstImgCum = Histogram.calcHistCum(histFstImg, fstImg);
        histSndImgCum = Histogram.calcHistCum(histSndImg, sndImg);

        for(int shade = 0; shade < histogramMatch.length; shade++) {
            histogramMatch[shade] = findTargetShadeClosest(shade, histFstImgCum, histSndImgCum);
        }

        for (int i = 0; i < fstImg.getWidth(); i++) {
            for(int j = 0; j < fstImg.getHeight(); j++) {
                int newPixel = Pixels.assemblePixel(histogramMatch[Pixels.getG(fstImg.getRGB(i,j))]);
                imgMatched.setRGB(i, j, newPixel);
            }
        }


        histogramMatching.showImage(SwingFXUtils.toFXImage(imgMatched, null));
    }
}
