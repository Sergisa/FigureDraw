public class Rectangle extends GeomteryFigure{
    float height;
    float width;
    private final String name="Мой прямоугольник";
    public Rectangle(float height, float width){
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
}
