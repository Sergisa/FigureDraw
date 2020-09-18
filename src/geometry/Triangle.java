package geometry;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends GeomteryFigure implements Figure{
    int a,b,c;
    public Triangle(int a, int b, int c){
        this(a, b, c, null);
    }

    public Triangle(int a, int b, int c, String name) {
        super(name);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double getPerimeter() {
        return a+b+c;
    }

    @Override
    public double getArea() {
        double p = getPerimeter()/2;
        return Math.sqrt(p*(p-a)*(p-b)*(p-c));
    }
}
