package geometry;

public class Ellipse extends GeomteryFigure{
    public float radius1, radius2;
    private Ellipse(float radius1, float radius2, Dot centerDot){
            super("name");
            this.radius1 = radius1;
            this.radius2 = radius2;
            mainDots.add(centerDot);
    }

    public static Ellipse createEllipse(float radius1, float radius2, Dot centerDot){
        return new Ellipse(radius1, radius2, centerDot);
    }

    public static Ellipse createCircle(float radius, Dot centerDot){
        return new Ellipse(radius,radius, centerDot);
    }
    public double getArea(){
        return Math.PI * this.radius1 * this.radius2;
    }
    public double getPerimeter(){
        return Math.PI * (this.radius1 + this.radius2);
    }
    public Dot getCenter(){
        return this.mainDots.get(0);
    }
    public void setCenter(Dot dot){
        this.mainDots.clear();
        this.mainDots.add(dot);
    }

    @Override
    public String toString() {
        return "Ellipse{" +
                "radius1=" + radius1 +
                ", radius2=" + radius2 +
                ", centerDot=[" + getCenter().x + ", " +getCenter().y+
                "]}";
    }
}
