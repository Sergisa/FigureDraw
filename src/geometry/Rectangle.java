package geometry;

public class Rectangle extends GeomteryFigure{
    public float height;
    public float width;
    static String type="Прямоугольник";

    public Rectangle(float height, float width) {
        this(height, width, null);
    }
    public Rectangle(float height, float width, String name){
        super(name);
        this.height = height;
        this.width = width;
    }
    public double getArea(){
        return this.height*this.width;
    }

    public double getPerimeter(){
        return 2*this.height + 2*this.width;
    }

}
