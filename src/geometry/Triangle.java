package geometry;

import java.util.Arrays;

public class Triangle extends GeomteryFigure{
    public double a;
    public double b;
    public double c;

    public Triangle(Dot... dots) {
        super("");
        mainDots.addAll(Arrays.asList(dots));
        
        a=getPairs().get(0).getDistance();
        b=getPairs().get(1).getDistance();
        c=getPairs().get(2).getDistance();
    }
}
