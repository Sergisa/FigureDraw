package geometry;

public class Rectangle extends GeomteryFigure{
    public float height;
    public float width;
    static String type="Прямоугольник";

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

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
