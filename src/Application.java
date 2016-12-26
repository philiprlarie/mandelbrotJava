/**
 * Created by Philip on 12/25/16.
 */
public class Application {
    public static void main(String[] args) {
        MandelbrotGrid mandelbrotGrid = new MandelbrotGrid(100, 100, 0, 0, 10, 1000);
        for (double[] row : mandelbrotGrid.getGrid()) {
            for (double val : row) {
                System.out.printf("%5.0f", val);
            }
            System.out.println("");
        }
    }
}
