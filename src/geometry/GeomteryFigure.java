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

    @Override
    public double getPerimeter() {
        double perimetr = 0;
        for (DotPair pair : getPairs()) {
            perimetr += pair.getDistance();
        }
        return perimetr;
    }

    @Override
    public double getArea() {
        double area = 0;
        for (int i = 0; i < mainDots.size(); i++){
            if(i == mainDots.size()-1){ // last iteration
                area += mainDots.get(i).x * mainDots.get(0).y - mainDots.get(0).x * mainDots.get(i).y;
            }else{
                area += mainDots.get(i).x * mainDots.get(i+1).y - mainDots.get(i+1).x * mainDots.get(i).y;
            }
        }
        return Math.abs(area)/2;
    }

    public List<DotPair> getPairs() {
        List<DotPair> dotPairList = new ArrayList<>();

        for (int i = 0; i < mainDots.size(); i++){
            if(i == mainDots.size()-1){ // last iteration
                dotPairList.add(new DotPair(mainDots.get(i),mainDots.get(0)));
            }else{
                dotPairList.add(new DotPair(mainDots.get(i),mainDots.get(i+1)));
            }
        }
        return dotPairList;
    }
}
