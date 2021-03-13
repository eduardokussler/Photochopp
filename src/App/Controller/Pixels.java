package App.Controller;

import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;

public class Pixels {
    public static int getR(int argb) {
        int redMask = 0x00ff0000;
        int r = argb & redMask;
        r = r >> 16;
        return r;
    }

    public static int getG(int argb) {
        int greenMask = 0x0000ff00;
        int g = argb & greenMask;
        g = g >> 8;
        return g;
    }
    public static int getB(int argb) {
        int blueMask = 0x000000ff;
        return argb & blueMask;
    }

    public static int assemblePixel(int red, int green, int blue) {
        int alpha = 255;
        int argb;
        argb = alpha;
        argb = argb << 8;
        argb += red;
        //Colocando o valor do vermelho na posição do vermelho
        argb = argb << 8;
        argb += green;
        //Colocando o valor do verde na posição do verde
        argb = argb << 8;
        //Colocando o valor do azul na posição do azul
        argb += blue;
        return argb;

    }
    public static int assemblePixel(int rgb) {
        int alpha = 255;
        int argb;
        argb = alpha;
        argb = argb << 8;
        argb += rgb;
        //Colocando o valor do vermelho na posição do vermelho
        argb = argb << 8;
        argb += rgb;
        //Colocando o valor do verde na posição do verde
        argb = argb << 8;
        //Colocando o valor do azul na posição do azul
        argb += rgb;
        return argb;
    }
    // Para testar se a imagem já está em tons de cinza
    // Seleciona um pixel aleatório e ve se os valores RGB são diferentes
    // Pode acontecer de que o pixel selecionado seja cinza, embora a  imagem em si não seja
    // É uma possibilidade remota e para os objetivos do trabalho e as limitações das bibliotecas,
    // Essa é uma solução ok
    public static boolean isGrayscale(BufferedImage img)  {
        int randPixel = img.getRGB((int)(Math.random() * img.getWidth()), (int)(Math.random() * img.getHeight()));
        if(Pixels.getR(randPixel) != Pixels.getG(randPixel) || Pixels.getR(randPixel) != Pixels.getB(randPixel) || Pixels.getB(randPixel) != Pixels.getG(randPixel)) {
           return false;
        } else {
            return true;
        }
    }

    public static BufferedImage luminance(BufferedImage img) {
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
}
