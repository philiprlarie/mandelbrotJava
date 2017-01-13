import java.awt.image.BufferedImage;

/**
 * Created by Philip on 1/10/17.
 */
public class MandelbrotImageOrangeBlack extends MandelbrotImage {
    public MandelbrotImageOrangeBlack(int width, int height, Coord center, double zoom, int maxIterations) {
        super(width, height, center, zoom, maxIterations);
    }

    protected BufferedImage createImage(double[][] mandelbrotGrid) {
        int height = mandelbrotGrid.length;
        int width = mandelbrotGrid[0].length;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int binaryColorValue = getBinaryColorValue(mandelbrotGrid[i][j]);
                img.setRGB(j, i, binaryColorValue);
            }
        }
        return img;
    }

    private int getBinaryColorValue(double gridValue) {
        int r = (int)Math.floor(255 * gridValue / maxIterations);
        int g = (int)Math.floor(150 * gridValue / maxIterations);
        int b = (int)Math.floor(50 * gridValue / maxIterations);
        return (r << 16) | (g << 8) | b;
    }
}
