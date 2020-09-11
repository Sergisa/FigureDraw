package geometry;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends GeomteryFigure implements Figure{
    int a,b,c;
    public Triangle(int length1, int length2, int length3){
        super("wf");
        this.a = length1;
        this.b = length2;
        this.c = length3;
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

    @Override
    public String getName() {
        return name;
    }
}
