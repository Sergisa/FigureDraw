public class Rectangle extends GeomteryFigure{
    double height,width;
    public Rectangle(int height, int width){
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
