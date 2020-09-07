public class Circle extends GeomteryFigure{
    float radius;
    public Circle(float radius){
        this.radius = radius;
    }

    public double getArea(){
        return Math.PI * Math.pow(this.radius, 2);
    }
    public double getPerimeter(){
        return 2*Math.PI * this.radius;
    }
}
