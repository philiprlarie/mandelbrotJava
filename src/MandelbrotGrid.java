/**
 * Created by Philip on 12/25/16.
 */
public class MandelbrotGrid {
    private double[][] grid;
    private int width;
    private int height;
    private Coord center;
    private double zoom;
    private int maxIterations;

    MandelbrotGrid (int width, int height, double centerX, double centerY, double zoom, int maxIterations) {
        this.width = width;
        this.height = height;
        this.center = new Coord(centerX, centerY);
        this.zoom = zoom; // number of vertical or horizontal grid points (pixels) that fit into one cartesian unit
        this.maxIterations = maxIterations;
        this.grid = generateGrid();
    }

    public double[][] getGrid () {
        return this.grid;
    }
    
    private double[][] generateGrid() {
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

    // Given a point, iterate the mandelbrot function until point has magnitude > 2 or max iterations. If we meet max iterations, assume point will never escape
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

class Coord {
    double x;
    double y;

    Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double squareDistance() {
        return x * x + y * y;
    }
}