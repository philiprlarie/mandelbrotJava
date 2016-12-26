import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotImageCreator {
    private BufferedImage img;

    public void createImage(double[][] mandelbrotGrid) {
        int height = mandelbrotGrid.length;
        int width = mandelbrotGrid[0].length;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int binaryColorValue = getBinaryColorValue(mandelbrotGrid[i][j]);
                img.setRGB(j, i, binaryColorValue);
            }
        }

//        File f = new File("MyFile.png");
//        try {
//            ImageIO.write(img, "PNG", f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public BufferedImage getImg() {
        return this.img;
    }


    private int getBinaryColorValue(double gridValue) {
        if (gridValue > 999) {
            return 0;
        }
        return (255 << 16) | (255 << 8) | 255;
    }

}
