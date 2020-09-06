public class Rectangle extends GeomteryFigure{
    double height;
    double width;
    public Rectangle(double height, double width){
        this.height = height;
        this.width = width;
    }

    public double getArea(){
        return this.height*this.width;
    }
    public double getPerimetr(){
        return 2*this.height + 2*this.width;
    }
}
