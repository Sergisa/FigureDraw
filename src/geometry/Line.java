package geometry;

import java.util.Objects;

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
    public static Line create(float[] a, float[] b){
        return create(
                new Dot(a[0],a[1]),
                new Dot(b[0],b[1])
        );
    }

    @Override
    public String toString() {
        return "Line{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(point1, line.point1) &&
                Objects.equals(point2, line.point2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point1, point2);
    }
}
