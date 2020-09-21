package geometry;

public class Line extends GeomteryObject {
    public Dot point1, point2;
    private Line(String name, Dot point1, Dot point2){
        //super(name);
        this.point1 = point1;
        this.point2 = point2;
    }

    public static Line create(Dot dot1, Dot dot2){
        return new Line(null, dot1, dot2);
    }


}
