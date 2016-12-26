//import javax.swing.*;
//import java.awt.*;

/**
 * Created by Philip on 12/25/16.
 */
public class Application {
    public static void main(String[] args) {
        MandelbrotGridCreator mandelbrotGridCreator = new MandelbrotGridCreator();
        double[][] mandelbrotGrid = mandelbrotGridCreator.createGrid(1000, 1000, 0, 0, 250, 1000);
//        for (double[] row : mandelbrotGrid) {
//            for (double val : row) {
//                System.out.printf("%5.0f", val);
//            }
//            System.out.println("");
//        }
        MandelbrotImageCreator mandelbrotImageCreator = new MandelbrotImageCreator();
        mandelbrotImageCreator.createImage(mandelbrotGrid);
    }
}
