import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotImage {
    protected static MandelbrotGridCreator mandelbrotGridCreator = new MandelbrotGridCreator();
    protected int width;
    protected int height;
    protected Coord center;
    protected double zoom;
    protected int maxIterations;
    protected double[][] mandelbrotGrid;
    protected BufferedImage image;

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

    public void saveImage() {
        int newWidth = 4000;
        int newHeight = (int)Math.ceil((double)newWidth / width * height);
        double newZoom = (double)newWidth / width * zoom;

        double[][] mandelbrotGrid = mandelbrotGridCreator.createGrid(newWidth, newHeight, center, newZoom, maxIterations);
        BufferedImage img = createImage(mandelbrotGrid);
        File f = new File("mandelbrot_" + center.x + "_" + center.y + "_" + newZoom + ".png");
        try {
            ImageIO.write(img, "PNG", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (gridValue > maxIterations - 1) {
            return 0;
        }
        return Integer.MAX_VALUE;
    }
}
