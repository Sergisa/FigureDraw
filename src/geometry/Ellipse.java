package geometry;

public class Ellipse extends GeomteryFigure{
    public float radius1, radius2;
    private Dot centerDot;
    private Ellipse(float radius1, float radius2){
        this(radius1, radius2, null);
    }

    private Ellipse(float radius1, float radius2, String name){
        super(name);
        this.radius1 = radius1;
        this.radius2 = radius2;
    }
    public static Ellipse createEllipse(float a, float b){
        return new Ellipse(a,b);
    }

    public static Ellipse createCircle(float radius){
        return new Ellipse(radius,radius);
    }
    public double getArea(){
        return Math.PI * this.radius1 * this.radius2;
    }
    public double getPerimeter(){
        return Math.PI * (this.radius1 + this.radius2);
    }
    public Dot getCenter(){
        return this.centerDot;
    }
    public void setCenter(Dot dot){
        this.mainDots.add(dot);
        this.centerDot = dot;
    }

    public Ellipse setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Ellipse{" +
                "radius1=" + radius1 +
                ", radius2=" + radius2 +
                ", centerDot=[" + centerDot.x + ", " +centerDot.y+
                "]}";
    }
}
