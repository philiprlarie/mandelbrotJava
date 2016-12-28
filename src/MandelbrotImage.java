import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotImage {
    private static MandelbrotGridCreator mandelbrotGridCreator = new MandelbrotGridCreator();
    private int width;
    private int height;
    private Coord center;
    private double zoom;
    private int maxIterations;
    private double[][] mandelbrotGrid;
    private BufferedImage image;

    public MandelbrotImage(int width, int height, Coord center, double zoom, int maxIterations) {
        this.width = width;
        this.height = height;
        this.center = center;
        this.zoom = zoom;
        this.maxIterations = maxIterations;
        this.mandelbrotGrid = mandelbrotGridCreator.createGrid(width, height, center, zoom, maxIterations);
        this.image = createImage(mandelbrotGrid);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Coord getCenter() {
        return center;
    }

    public double getZoom() {
        return zoom;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void saveImage(int width, int height, Coord center, double zoom, int maxIterations) {
        double[][] mandelbrotGrid = mandelbrotGridCreator.createGrid(width, height, center, zoom, maxIterations);
        BufferedImage img = createImage(mandelbrotGrid);
        File f = new File("MyFile.png");
        try {
            ImageIO.write(img, "PNG", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage createImage(double[][] mandelbrotGrid) {
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
        if (gridValue > maxIterations - 1) {
            return 0;
        }
        return (255 << 16) | (255 << 8) | 255;
    }
}
