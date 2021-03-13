package App.Controller;

import App.Main;
import App.Controller.Histogram;
import App.Controller.Pixels;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Spinner;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;



public class WindowController {
    @FXML
    private ImageView originalImage;

    @FXML
    private ImageView targetImage;

    @FXML
    private Spinner spnColors;

    @FXML
    private Spinner spnBrightness;

    @FXML
    private Spinner spnContrast;

    @FXML
    private Spinner spnXfactor;

    @FXML
    private Spinner spnYfactor;



    public static void showView() throws IOException {
        Parent root = FXMLLoader.load(WindowController.class.getResource("Window.fxml"));
        Main.stage.setTitle("Photochopp");
        Main.stage.setScene(new Scene(root, 1500, 900));
        Main.stage.show();

    }

    public void setOriginalImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        fileChooser.setSelectedExtensionFilter(fileChooser.getSelectedExtensionFilter());
        File imagem;
        imagem = fileChooser.showOpenDialog(Main.stage);
        BufferedImage image = ImageIO.read(imagem);

        Image showImage = null;
        showImage = SwingFXUtils.toFXImage(image, (WritableImage) showImage);
        double imageHeight = showImage.getHeight();
        double imageWidth = showImage.getWidth();
        //originalImage.setViewport(new Rectangle2D(imageWidth,imageHeight,imageWidth,imageHeight));
        targetImage.setImage(null);
        originalImage.setImage(showImage);
    }

    public void copyToTarget(){
        targetImage.setImage(originalImage.getImage());
    }

    public void calcLuminance() {
        BufferedImage original = null;
        if (targetImage.getImage() != null){
            original = SwingFXUtils.fromFXImage(targetImage.getImage(),original);
        } else {
            original = SwingFXUtils.fromFXImage(originalImage.getImage(), original);
        }
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
        for(int i = 0; i < original.getWidth(); i++){
            for(int j = 0; j < original.getHeight(); j++){
                pixel = original.getRGB(i,j);
                blue = pixel & blueMask;
                blue = (int) (blue * 0.114);
                red = pixel & redMask;
                // Fazendo o shift para o valor do vermelho não ter peso maior
                red =(int) ((red >> 16) * 0.299);
                green = pixel & greenMask;
                // Fazendo o shift para o valor do verde não ter peso maior
                green =(int) ((green >> 8) * 0.587);
                luminance = red + green + blue;
                argb = calcGrayscale(luminance, alpha);
                original.setRGB(i,j, argb);
            }
        }
        Image newImage = null;
        targetImage.setImage(SwingFXUtils.toFXImage(original, (WritableImage) newImage));


    }

    public void flipImageVertically() {
        Image original = originalImage.getImage();
        if (targetImage.getImage() != null){
            original = targetImage.getImage();
        }
        BufferedImage imageToFlip = null;
        imageToFlip = SwingFXUtils.fromFXImage(original, imageToFlip);
        int pixel;
        BufferedImage imageFlipped = new BufferedImage(imageToFlip.getWidth(), imageToFlip.getHeight(), imageToFlip.getType());
        int imageHeight = imageToFlip.getHeight();
        for(int row=0; row < imageHeight; row++) {
            for(int column = 0; column < imageToFlip.getWidth(); column++){
                pixel = imageToFlip.getRGB(column,row);

                imageFlipped.setRGB(column,imageHeight-1-row,pixel);
            }
        }
        Image result = null;
        result = SwingFXUtils.toFXImage(imageFlipped, (WritableImage) result);
        targetImage.setImage(result);
    }

    public void flipImageHorizontally() {
        Image original = originalImage.getImage();
        if (targetImage.getImage() != null){
            original = targetImage.getImage();
        }
        BufferedImage imageToFlip = null;
        imageToFlip = SwingFXUtils.fromFXImage(original, imageToFlip);
        int pixel;
        BufferedImage imageFlipped = new BufferedImage(imageToFlip.getWidth(), imageToFlip.getHeight(), imageToFlip.getType());
        int imageWidth = imageToFlip.getWidth();
        for(int column=0; column < imageWidth; column++) {
            for(int row = 0; row < imageToFlip.getHeight(); row++){
                pixel = imageToFlip.getRGB(column,row);

                imageFlipped.setRGB(imageWidth - column - 1,row,pixel);
            }
        }
        Image result = null;
        result = SwingFXUtils.toFXImage(imageFlipped, (WritableImage) result);
        targetImage.setImage(result);
    }

    public void saveImage() throws IOException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.setInitialFileName("image");
        fileChooser.getExtensionFilters().addAll(
                //new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File saveFile = fileChooser.showSaveDialog(Main.stage);
        String path = saveFile.getAbsolutePath();
        String extension = path.substring(path.length()-3);
        // Se a extensão for jpg, ignorar o alpha
        BufferedImage saveImage = null;
        if(targetImage.getImage() == null)
            targetImage.setImage(originalImage.getImage());
        if(extension.equals("jpg")){
            saveImage = new BufferedImage((int)targetImage.getImage().getWidth(), (int)targetImage.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        else {
            saveImage = new BufferedImage((int)targetImage.getImage().getWidth(), (int)targetImage.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        if (saveFile != null) {
            BufferedImage saveImageTemp = SwingFXUtils.fromFXImage(targetImage.getImage(),
                    saveImage);

            saveImage.getGraphics().drawImage(saveImageTemp,0, 0, null);

            ImageIO.write((RenderedImage) saveImage,extension , saveFile);
        }


    }

    private int getMaxLuminance(BufferedImage img){
        int blueMask = 0x000000ff;
        int maxLuminance = img.getRGB(0,0) & blueMask;
        int currentPixel;
        // A luminance não varia entre as cores, então basta comparar um dos valores
        int luminance;
        for(int row = 0; row < img.getHeight(); row++) {
            for (int column = 0; column < img.getWidth(); column++){
                currentPixel = img.getRGB(column, row);
                luminance = currentPixel & blueMask;
                if (Integer.compareUnsigned(maxLuminance, luminance) < 0){
                    maxLuminance = luminance;
                }
            }
        }
        return maxLuminance;
    }

    private int getMinLuminance(BufferedImage img) {
        int blueMask = 0x000000ff;
        int minLuminance = img.getRGB(0,0) & blueMask;
        int currentPixel;
        // A luminance não varia entre as cores, então basta comparar um dos valores
        int luminance;

        for(int row = 0; row < img.getHeight(); row++) {
            for (int column = 0; column < img.getWidth(); column++){
                currentPixel = img.getRGB(column, row);
                luminance = currentPixel & blueMask;
                if (Integer.compareUnsigned(minLuminance, luminance) > 0){
                    minLuminance = luminance;
                }
            }
        }
        return minLuminance;
    }


    private int calcGrayscale(int luminance, int alpha){
        int argb;
        argb = alpha;
        argb = argb << 8;
        argb += luminance;
        //Colocando a luminancia na posição do vermelho
        argb = argb << 8;
        argb += luminance;
        //Colocando a luminancia na posição do verde
        argb = argb << 8;
        //Colocando a luminancia na posição do azul
        argb += luminance;
        return argb;
    }

    public void quantize() {
       int blueMask = 0x000000ff;
       int quantColors = (int)spnColors.getValueFactory().getValue();
       // Sempre usa de base a imagem originalc
       if(!Pixels.isGrayscale(SwingFXUtils.fromFXImage(originalImage.getImage(), null))){
           calcLuminance();
       } else {
           copyToTarget();
       }
       BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
       int tamMax = getMaxLuminance(image);
       int tamMin = getMinLuminance(image);
       int tamInt = tamMax - tamMin + 1;

       if(quantColors < tamInt) {
           int tb = tamInt/quantColors;
           int currentPixel;
           int currentLum;
           int newLum;
           // Calculando o tamanho do intervalo
           int size = 256 / quantColors;
           // Calculando a mediana
           int mean = (size - 1) / 2;
           int newRGB;
           for(int row = 0; row < image.getHeight(); row++) {
               for (int column = 0; column < image.getWidth(); column++){
                   currentPixel = image.getRGB(column, row);
                   currentLum = currentPixel & blueMask;
                   // Calculando em qual intervalo a luminancia atual cai
                   newLum = currentLum / size;
                   // Calculando o valor o mais proximo do centro do intervalo
                   newLum = newLum*size + mean;
                   newRGB = calcGrayscale(newLum, 255);
                   image.setRGB(column,row, newRGB);
               }
           }
           targetImage.setImage(SwingFXUtils.toFXImage(image, null));
       }

    }


    private void createHistogramWindow(String name) throws IOException {
        // Creating another window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Histogram.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Photochopp - Histogram " + name);
        stage.setScene(new Scene(root, 255, 255));
        stage.show();
        Histogram HistogramControler = loader.getController();

        // Calculando a quantidade de tons da imagem
        BufferedImage image = null;
        image = SwingFXUtils.fromFXImage(targetImage.getImage(), image);
        // Calculating the histogram
        int hist[] = new int [256];
        Histogram.histCalc(hist, image);

        Image returnImage = createImageFromHistogramDist(hist);
        HistogramControler.showHistogram(returnImage);
    }
    public void histogramCalc() throws Exception {

        if(!Pixels.isGrayscale(SwingFXUtils.fromFXImage(originalImage.getImage(), null))) {
            calcLuminance();
        } else {
            targetImage.setImage(originalImage.getImage());
        }
        createHistogramWindow("");
    }
    private Image createImageFromHistogramDist(int histogram[]) {
        int maxTones = 0;
        int color = 0xff000000;
        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        for (int tone = 0; tone < histogram.length; tone++) {
            if (histogram[tone] > maxTones) {
                maxTones = histogram[tone];
            }
        }
        for (int i = 0; i < 256; i++) {
            int height =(int) ((histogram[i] / (float)maxTones) * 256);
            for (int j = 0; j < height; j++) {
                image.setRGB(i, image.getHeight() - j - 1, color);
            }
        }
        return SwingFXUtils.toFXImage(image, null);
    }


    private int calcNewBrightness(int rgb, int b) {
        int blue = Pixels.getB(rgb);
        int green = Pixels.getG(rgb);
        int red = Pixels.getR(rgb);

        // Garantindo que o valor do pixel não passe de 255
        blue = Math.min(blue + b, 255);
        green = Math.min(green + b, 255);
        red = Math.min(red + b, 255);
        // Garantindo que o valor do pixel não é menor que 0
        blue = Math.max(blue, 0);
        green = Math.max(green, 0);
        red = Math.max(red, 0);

        return Pixels.assemblePixel(red, green, blue);



    }

    public void adjustBrightness() {
        if (targetImage.getImage() == null)
            targetImage.setImage(originalImage.getImage());
        int b = (int) spnBrightness.getValueFactory().getValue();
        BufferedImage img = SwingFXUtils.fromFXImage(targetImage.getImage(),null );
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                img.setRGB(i,j, calcNewBrightness(img.getRGB(i,j), b));
            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(img, null));

    }

    private int calcNewContrast(int rgb, double b) {
        int blue = Pixels.getB(rgb);
        int green = Pixels.getG(rgb);
        int red = Pixels.getR(rgb);

        // Garantindo que o valor do pixel não passe de 255
        blue = Math.min((int)(blue * b), 255);
        green = Math.min((int) (green * b), 255);
        red = Math.min((int)(red * b), 255);


        return Pixels.assemblePixel(red, green, blue);
    }

    public void adjustContrast() {
        if (targetImage.getImage() == null)
            targetImage.setImage(originalImage.getImage());
        double b = (double) spnContrast.getValueFactory().getValue();
        BufferedImage img = SwingFXUtils.fromFXImage(targetImage.getImage(),null );
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                img.setRGB(i,j, calcNewContrast(img.getRGB(i,j), b));
            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(img, null));

    }

    private int calcNegativePixel(int rgb) {
        int blue = 255 - Pixels.getB(rgb);
        int green = 255 - Pixels.getG(rgb);
        int red = 255 - Pixels.getR(rgb);


        return Pixels.assemblePixel(red, green, blue);

    }
    public void calcNegative() {
        if (targetImage.getImage() == null)
            targetImage.setImage(originalImage.getImage());
        BufferedImage img = SwingFXUtils.fromFXImage(targetImage.getImage(),null );
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                img.setRGB(i,j, calcNegativePixel(img.getRGB(i,j)));
            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(img, null));


    }

    public void histEqualization() throws Exception {

        if(!Pixels.isGrayscale(SwingFXUtils.fromFXImage(originalImage.getImage(), null))) {
            calcLuminance();
        } else {
            targetImage.setImage(originalImage.getImage());
        }
        createHistogramWindow("before equalization");
        // scaling factor
        double alpha = 255.0f / (targetImage.getImage().getHeight() * targetImage.getImage().getWidth());
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        int hist[] = new int [256];

        int hist_cum[] = new int [256];
        Histogram.histCalc(hist, image);
        hist_cum = Histogram.calcHistCum(hist, image);

        for (int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int newPixel = Pixels.assemblePixel(hist_cum[Pixels.getG(image.getRGB(i,j))]);
                image.setRGB(i, j, newPixel);
            }
        }

        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
        createHistogramWindow("after equalization");
    }

    public void createHistMatchWindow() throws IOException {
        // Creating another window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HistogramMatching.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Photochopp - Histogram Matching");
        stage.setScene(new Scene(root, 1500, 900));
        stage.show();
        HistogramMatching histogramMatching = loader.getController();

    }

    public void zoomOut() {
        int xFactor = (int)spnXfactor.getValueFactory().getValue();
        int yFactor = (int)spnYfactor.getValueFactory().getValue();
        if (targetImage.getImage() == null) {
            targetImage.setImage(originalImage.getImage());
        }
        int red;
        int green;
        int blue;
        BufferedImage srcImg = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        BufferedImage resultImg = new BufferedImage((int)targetImage.getImage().getWidth() / xFactor, (int)targetImage.getImage().getHeight() / yFactor , BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < targetImage.getImage().getWidth(); x += xFactor) {
            for(int y = 0; y < targetImage.getImage().getHeight(); y += yFactor) {
                red = 0;
                green = 0;
                blue = 0;
                for(int i = x; i < x + xFactor; i++) {
                    if(i >= srcImg.getWidth()) {
                        break;
                    }
                    for(int j = y; j < y + yFactor; j++) {
                        if(j >= srcImg.getHeight()){
                            break;
                        }
                        red += Pixels.getR(srcImg.getRGB(i, j));
                        green += Pixels.getG(srcImg.getRGB(i, j));
                        blue += Pixels.getB(srcImg.getRGB(i, j));
                    }
                }
                // Calculando as médias
                red = red / (xFactor * yFactor);
                green = green / (xFactor * yFactor);
                blue = blue / (xFactor * yFactor);
                int xCoord = x/xFactor;
                int yCoord = y/yFactor;
                if(xCoord > resultImg.getWidth())
                    xCoord = resultImg.getWidth() - 1;
                if(yCoord > resultImg.getHeight())
                    yCoord = resultImg.getHeight() - 1;
                resultImg.setRGB(xCoord, yCoord, Pixels.assemblePixel(red, green, blue));
            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(resultImg, null));
    }


    public void zoomIn() {
        if(targetImage.getImage() == null) {
            targetImage.setImage(originalImage.getImage());
        }
        int factor = 2;
        BufferedImage srcImg = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        BufferedImage resultImg = new BufferedImage(srcImg.getWidth() * factor, srcImg.getHeight() * factor, BufferedImage.TYPE_INT_ARGB);

        // Copiando os valores já conhecidos
        for (int i = 0; i < srcImg.getWidth(); i++) {
            for(int j = 0; j < srcImg.getHeight(); j++) {
                resultImg.setRGB(i*factor, j*factor, srcImg.getRGB(i, j));
            }
        }
        int red;
        int green;
        int blue;
        // Fazendo interpolação vertical
        for (int i = 0; i < resultImg.getWidth(); i++) {
            for(int j = 1; j < resultImg.getHeight() - 1; j += factor) {
                red = Pixels.getR(resultImg.getRGB(i, j - 1));
                green = Pixels.getG(resultImg.getRGB(i, j - 1));
                blue = Pixels.getB(resultImg.getRGB(i, j - 1));

                red += Pixels.getR(resultImg.getRGB(i, j + 1));
                green += Pixels.getG(resultImg.getRGB(i, j + 1));
                blue += Pixels.getB(resultImg.getRGB(i, j + 1));

                red = red / 2;
                green = green / 2;
                blue = blue / 2;

                resultImg.setRGB(i, j, Pixels.assemblePixel(red, green, blue));
            }
        }

        // Fazendo a interpolação horizontal
        for (int i = 1; i < resultImg.getWidth() - 1; i += factor) {
            for(int j = 0; j < resultImg.getHeight(); j++) {
                red = Pixels.getR(resultImg.getRGB(i - 1, j));
                green = Pixels.getG(resultImg.getRGB(i - 1, j));
                blue = Pixels.getB(resultImg.getRGB(i - 1, j));

                red += Pixels.getR(resultImg.getRGB(i + 1, j));
                green += Pixels.getG(resultImg.getRGB(i + 1, j));
                blue += Pixels.getB(resultImg.getRGB(i + 1, j));

                red = red / 2;
                green = green / 2;
                blue = blue / 2;
                resultImg.setRGB(i, j, Pixels.assemblePixel(red, green, blue));
            }
        }



        targetImage.setImage(SwingFXUtils.toFXImage(resultImg, null));

    }

    private BufferedImage convolution(float [][] filter, BufferedImage image) {
        BufferedImage resultImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        if(!Pixels.isGrayscale(image)) {
            image = Pixels.luminance(image);
        }
        // Altura original
        int N = image.getHeight();
        // Largura original
        int M = image.getWidth();

        // altura
        int n2 = (int)Math.floor((double)filter.length/ 2);
        // largura
        int m2 = (int)Math.floor((double)filter[0].length / 2);
        double sum;

        for(int y = n2; y < N - n2 - 1; y++) {
            for(int x = m2; x < M - m2 -1; x++) {
                sum = 0f;
                for(int k = -n2; k <= n2; k++) {
                    for(int j = -m2; j <= m2; j++) {
                        sum += filter[m2 + j][n2 + k] * Pixels.getB(image.getRGB(x-j, y-k));
                    }
                }

                sum = Math.max(0, sum);
                sum = Math.min(255, sum);

                resultImg.setRGB(x, y, Pixels.assemblePixel((int)sum));
            }
        }

        return resultImg;
    }


    public void gaussianFilter() {
        float[][] kernel = new float[3][3];
        kernel [0][0] = 0.0625f; kernel [0][1] = 0.125f; kernel [0][2] = 0.0625f;
        kernel [1][0] = 0.125f; kernel [1][1] = 0.25f; kernel [1][2] = 0.125f;
        kernel [2][0] = 0.0625f; kernel [2][1] = 0.125f; kernel [2][2] = 0.0625f;
        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);


        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void laplacianFilter() {
        float[][] kernel = {
                {0, -1, 0},
                {-1, 4, -1},
                {0, -1, 0},
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));


    }

    public void highPassFilter() {
        float[][] kernel = {
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1}
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                //pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void prewittXFilter() {
        float[][] kernel = {
                {-1, 0, 1},
                {-1, 0, 1},
                {-1, 0, 1}
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void prewittYFilter() {
        float[][] kernel = {
                {-1, -1, -1},
                {-0, 0, 0},
                {1, 1, 1}
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void sobelXFilter() {
        float[][] kernel = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }

    public void sobelYFilter() {
        float[][] kernel = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        if(targetImage.getImage() == null) {
            copyToTarget();
        }
        int pixel;
        BufferedImage image = SwingFXUtils.fromFXImage(targetImage.getImage(), null);
        image = convolution(kernel, image);
        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                pixel = Pixels.getR(image.getRGB(i, j));
                pixel += 127;
                pixel = Math.max(0, pixel);
                pixel = Math.min(255, pixel);
                image.setRGB(i, j, Pixels.assemblePixel(pixel));

            }
        }
        targetImage.setImage(SwingFXUtils.toFXImage(image, null));
    }


}

