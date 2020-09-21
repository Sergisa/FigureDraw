package geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GeomteryFigure implements Figure{
    private Color color;
    private double area;
    private double perimetr;
    protected String name;
    protected List<Dot> mainDots;
    public GeomteryFigure(String name){
        this.name = name;
        mainDots = new ArrayList<>();
    }
    @Override
    public String getName() {
        return this.name;
    }
    public void rotate(float angle){
        this.rotate(angle, new Dot(0,0));
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }
    public void rotate(float angle, Dot rotationPoint) {
        for (Dot dot : mainDots) {
            dot.rotate(angle, rotationPoint);
        }
    }

    @Override
    public String toString() {
        return "GeomteryFigure{" +
                "color=" + color +
                ", area=" + area +
                ", perimetr=" + perimetr +
                ", name='" + name + '\'' +
                ", mainDots=" + mainDots +
                '}';
    }
}
