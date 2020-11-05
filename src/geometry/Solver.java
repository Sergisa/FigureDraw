package geometry;

import java.awt.geom.Point2D;

public class Solver {
    static double distance(Dot d1, Dot d2){
        return Point2D.distance(d1.x,d1.y,d2.x,d2.y);
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Point2D.distance(x1, y1, x2, y2);
    }
}
