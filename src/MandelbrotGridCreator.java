import static com.sun.tools.doclint.Entity.mu;
import static com.sun.tools.doclint.Entity.real;

/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotGridCreator {
    // O(width * height * maxIteractions)
    public double[][] createGrid(int width, int height, Coord center, double zoom, int maxIterations) {
        return createGrid(width, height, center, zoom, maxIterations, false);
    }
    // verbose
    public double[][] createGrid(int width, int height, Coord center, double zoom, int maxIterations, boolean verbose) {
        int percentCompletion = Integer.MIN_VALUE;
        double[][] grid = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double x = center.x + (j - width / 2.0) / zoom;
                double y = center.y + (i - height / 2.0) / zoom;
                Coord curPoint = new Coord(x, y);
                double mandelbrotValue = generateMandelbrotValue(curPoint, maxIterations);
                grid[i][j] = mandelbrotValue;
            }
            int progress = (int) (100 * (double) i / height);
            if (verbose && progress != percentCompletion) { // print progress in one percent increments
                percentCompletion = progress;
                System.out.println(progress + "%");
            }
        }
        return grid;
    }


    // Given a point, iterate the mandelbrot function until orbit has magnitude > escapeRadius or we have performed a set max number of iterations. If we meet max iterations, assume point will never escape. O(maxIterations)
    // escape value should have min >= 0 and max <= numIterations
    private double generateMandelbrotValue(Coord point, int maxIterations) {
        double mandelbrotValue;
        double escapeRadius = 2000;
        Coord curIterationValue = new Coord(0, 0);
        int i = 1;
        while (i < maxIterations) {
            curIterationValue = singleMandelbrotIteration(point, curIterationValue);
            if (curIterationValue.squareDistance() > escapeRadius * escapeRadius) {
                break;
            }
            i++;
        }

//        // this way returns simple integer escape values.
//        mandelbrotValue = i;
//        return mandelbrotValue;

        // Smooth Escape Iteration Counts. see http://linas.org/art-gallery/escape/escape.html
        if (curIterationValue.squareDistance() < 2 * 2 * 2 * 2) {
            // current iteration value is too small, point didn't escape
            mandelbrotValue = maxIterations;
        } else {
            double mu = i + 1 - Math.log(Math.log(curIterationValue.squareDistance()) / 2) / Math.log(2);
            mandelbrotValue = mu > maxIterations ? maxIterations : mu; // set the max escape value at maxIterations
            mandelbrotValue = mandelbrotValue < 0 ? 0 : mandelbrotValue; // set the min escape value at 0
        }

        return mandelbrotValue;
    }



    private Coord singleMandelbrotIteration(Coord startPoint, Coord prevIterationValue) {
        double newX = prevIterationValue.x * prevIterationValue.x - prevIterationValue.y * prevIterationValue.y + startPoint.x;
        double newY = 2 * prevIterationValue.x * prevIterationValue.y + startPoint.y;
        return new Coord(newX, newY);
    }
}
