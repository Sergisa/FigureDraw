package geometry;

import java.util.ArrayList;
import java.util.List;

public abstract class GeomteryFigure implements Figure{
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
    private void df(){}
    public void rotate(float angle){
        this.rotate(angle, new Dot(0,0));
    }

    public void rotate(float angle, Dot rotationPoint){
        for (Dot dot : mainDots) {
            dot.rotate(angle*Math.PI/180, rotationPoint);
        }
    }
}
