/**
 * Created by Philip on 12/26/16.
 */
public class Coord {
    public double x;
    public double y;

    Coord(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double squareDistance() {
        return x * x + y * y;
    }
}
