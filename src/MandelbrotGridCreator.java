/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotGridCreator {
    // O(width * height * maxIteractions)
    public double[][] createGrid(int width, int height, Coord center, double zoom, int maxIterations) {
        double[][] grid = generateGrid(width, height, center, zoom, maxIterations);
        return grid;
    }
    
    private double[][] generateGrid(int width, int height, Coord center, double zoom, int maxIterations) {
        double[][] grid = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double x = center.x + (j - width / 2.0) / zoom;
                double y = center.y + (i - height / 2.0) / zoom;
                Coord curPoint = new Coord(x, y);
                double mandelbrotValue = generateMandelbrotValue(curPoint, maxIterations);
                grid[i][j] = mandelbrotValue;
            }
        }
        return grid;
    }

    // Given a point, iterate the mandelbrot function until point has magnitude > 2 or max iterations. If we meet max iterations, assume point will never escape. O(maxIterations)
    private double generateMandelbrotValue(Coord point, int maxIterations) {
        double mandelbrotValue;
        double escapeRadius = 2;
        Coord curIterationValue = new Coord(0, 0);
        int i = 1;
        while (i < maxIterations) {
            curIterationValue = singleMandelbrotIteration(point, curIterationValue);
            if (curIterationValue.squareDistance() > escapeRadius * escapeRadius) {
                break;
            }
            i++;
        }
        mandelbrotValue = i;
        return mandelbrotValue;
    }

    private Coord singleMandelbrotIteration(Coord startPoint, Coord prevIterationValue) {
        double newX = prevIterationValue.x * prevIterationValue.x - prevIterationValue.y * prevIterationValue.y + startPoint.x;
        double newY = 2 * prevIterationValue.x * prevIterationValue.y + startPoint.y;
        return new Coord(newX, newY);
    }
}
