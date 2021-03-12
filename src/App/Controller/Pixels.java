package App.Controller;

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
}
