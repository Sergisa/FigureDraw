package geometry;

public class Circle extends GeomteryFigure{
    public float radius;
    public Circle(float radius){
        this(radius, null);
    }

    public Circle(float radius, String name){
        super(name);
        this.radius = radius;
    }

    public double getArea(){
        return Math.PI * Math.pow(this.radius, 2);
    }
    public double getPerimeter(){
        return 2*Math.PI * this.radius;
    }
}
