package geometry;

import java.awt.geom.Point2D;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Solver {
    static double distance(Dot d1, Dot d2){
        return Point2D.distance(d1.x,d1.y,d2.x,d2.y);
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return Point2D.distance(x1, y1, x2, y2);
    }

    private static boolean intersect(double a, double b, double c, double d){
        double buf;
        if (a > b){
            buf=a;
            a=b;
            b=buf;
        }
        if (c > d){
            buf=c;
            c=d;
            d=buf;
        }
        return max(a,c) <= min(b,d);
    }
    private static double ar(Dot a, Dot b, Dot c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static double doubleArea(Dot ...a){
        return doubleArea(new DotList(Arrays.asList(a)));
    }
    public static double doubleArea(DotList points){
        double area = 0;
        for (int i = 0; i < points.size(); i++){
            if(i == points.size()-1){ // last iteration
                area += points.get(i).x * points.get(0).y - points.get(0).x * points.get(i).y;
            }else{
                area += points.get(i).x * points.get(i+1).y - points.get(i+1).x * points.get(i).y;
            }
        }
        return area;
    }

    public static double area(Dot ...a){
        return area(new DotList(Arrays.asList(a)));
    }
    public static double area(DotList points){
        return Math.abs(doubleArea(points))/2;
    }

    static boolean crosses(Dot a, Dot b, Dot c, Dot d){
        return intersect(a.x, b.x, c.x, d.x)
                && intersect(a.y, b.y, c.y, d.y)
                && doubleArea(a,b,c) * doubleArea(a,b,d) <= 0
                && doubleArea(c,d,a) * doubleArea(c,d,b) <= 0;
    }

    public static boolean crosses(Line ab, Line cd){
        return crosses(
                ab.point1,
                ab.point2,
                cd.point1,
                cd.point2
        );
    }
}
