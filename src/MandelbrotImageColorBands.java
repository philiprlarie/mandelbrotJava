import java.awt.image.BufferedImage;

import static com.sun.tools.doclint.Entity.reg;

/**
 * Created by plarie on 1/11/17.
 */
public class MandelbrotImageColorBands extends MandelbrotImage {
    int[] escapeValsHistogram;
    double[] escapeValsIntegral;

    public MandelbrotImageColorBands(int width, int height, Coord center, double zoom, int maxIterations) {
        super(width, height, center, zoom, maxIterations);
    }

    @Override
    protected BufferedImage createImage(double[][] mandelbrotGrid) {
        int height = mandelbrotGrid.length;
        int width = mandelbrotGrid[0].length;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        fillHistogram();
        fillEscapeValsIntegral();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int binaryColorValue = getBinaryColorValue(mandelbrotGrid[i][j]);
                img.setRGB(j, i, binaryColorValue);
            }
        }
        return img;
    }

    private void fillHistogram() {
        // the escape values should have a min value of 0 and a max value of maxIterations
        escapeValsHistogram = new int[maxIterations + 1];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int intEscapeVal = (int)Math.floor(mandelbrotGrid[row][col]);
                escapeValsHistogram[intEscapeVal] = escapeValsHistogram[intEscapeVal] + 1;
            }
        }
    }

    // this array is essentially the integral of the escapeValsHistogram. It tells us what fraction of the escape vals fall below a certain value. For example, at index 10 of escapeValsHistogram, we store the decimal value of the fraction of escape vals that are strictly less than 10.
    private void fillEscapeValsIntegral() {
        int numPixels = width * height;
        escapeValsIntegral = new double[maxIterations + 1];
        for (int i = 1; i <= maxIterations; i++) {
            escapeValsIntegral[i] = escapeValsIntegral[i - 1] + (double)escapeValsHistogram[i - 1] / numPixels;
        }
    }

    private int getBinaryColorValue(double gridValue) {
        // assume that escape vals are evenly spread within their histogram column.
        // all escape vals in the final column of the histogram (all points where escape val == maxIterations) will be treated separately and colored the same color.
        // we want to spread evenly in bands the remaining pixels.

        double[] bandWidths = { 0, .2, .4, .8, 1 };
        int[][] bandColors = {
                { 255, 255, 255 },
                { 255, 255, 255 },
                {   0,   0,   0 },
                { 255, 255, 255 },
                { 255, 255, 255 }
        };

        // escape val is in final column of histogram. they all get the same band color
        if ((int)Math.floor(gridValue) >= maxIterations) {
            return rgbArrayToBinaryColorValue(bandColors[bandColors.length - 1]);
        }

        double percentLessFloor = escapeValsIntegral[(int)Math.floor(gridValue)];
        double percentLessCeil = escapeValsIntegral[(int)Math.ceil(gridValue)];
        double decimalPartOfGridVal = gridValue - Math.floor(gridValue);
        // assuming gridValues are spread evenly in their histogram columns, a grid value that is closer to the following column will have a greater precent of grid values less than it. This val should always be less than or equal to 1
        double adjustedPercent = percentLessFloor + decimalPartOfGridVal * (percentLessCeil - percentLessFloor);

        double maxPercent = escapeValsIntegral[escapeValsIntegral.length - 1];
        // we want to find the percent of gridVals this is greater than if we ignore all the grid vals in the last column of the histogram
        double percentIgnoreMaxVals = adjustedPercent / maxPercent;

        // get the rgb values for the given percentIgnoreMaxVals
        int[] rgb = { 0, 0, 0};
        for (int i = 0; i < bandColors.length; i++) {
            if (percentIgnoreMaxVals > bandWidths[i] && percentIgnoreMaxVals <= bandWidths[i + 1]) {
                double r = bandColors[i][0] * (bandWidths[i + 1] - percentIgnoreMaxVals) / (bandWidths[i + 1] - bandWidths[i]) + bandColors[i + 1][0] * (percentIgnoreMaxVals - bandWidths[i]) / (bandWidths[i + 1] - bandWidths[i]);
                double g = bandColors[i][1] * (bandWidths[i + 1] - percentIgnoreMaxVals) / (bandWidths[i + 1] - bandWidths[i]) + bandColors[i + 1][1] * (percentIgnoreMaxVals - bandWidths[i]) / (bandWidths[i + 1] - bandWidths[i]);
                double b = bandColors[i][2] * (bandWidths[i + 1] - percentIgnoreMaxVals) / (bandWidths[i + 1] - bandWidths[i]) + bandColors[i + 1][2] * (percentIgnoreMaxVals - bandWidths[i]) / (bandWidths[i + 1] - bandWidths[i]);
                // r < 0 || g < 0 || b < 0 || r > 255 || g > 255 || b > 255;
                rgb[0] = (int)Math.floor(r);
                rgb[1] = (int)Math.floor(g);
                rgb[2] = (int)Math.floor(b);
                break;
            }
        }

        return rgbArrayToBinaryColorValue(rgb);
    }

    private int rgbArrayToBinaryColorValue(int[] rgbArray) {
        byte r = (byte) rgbArray[0];
        byte g = (byte) rgbArray[1];
        byte b = (byte) rgbArray[2];
        System.out.println(Integer.toHexString((r << 16) | (g << 8) | b));
        return (r << 16) | (g << 8) | b;
    }
}