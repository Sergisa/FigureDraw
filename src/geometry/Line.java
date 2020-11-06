package geometry;

import java.awt.*;
import java.util.Objects;

public class Line extends GeomteryObject {
    public Dot point1;
    public Dot point2;
    public Line(Dot point1, Dot point2){
        //super(name);
        this.point1 = point1;
        this.point2 = point2;
    }

    private Line(Builder builder) {
        setColor(builder.color);
        point1 = builder.point1;
        point2 = builder.point2;
    }

    public Dot getPoint1() {
        return point1;
    }

    public Line setPoint1(Dot point1) {
        this.point1 = point1;
        return this;
    }

    public Dot getPoint2() {
        return point2;
    }

    public Line setPoint2(Dot point2) {
        this.point2 = point2;
        return this;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Line copy) {
        Builder builder = new Builder();
        builder.color = copy.getColor();
        builder.point1 = copy.getPoint1();
        builder.point2 = copy.getPoint2();
        return builder;
    }

    public Dot getCenter(){
        return new Dot((point1.x + point2.x)/2, (point1.y + point2.y)/2 );
    }

    public boolean crosses(Line secondLine){
        return Solver.crosses(this, secondLine);
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

    public static final class Builder {
        private Color color;
        private Dot point1;
        private Dot point2;

        private Builder() {
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder setPoint1(Dot point1) {
            this.point1 = point1;
            return this;
        }

        public Builder setPoint2(Dot point2) {
            this.point2 = point2;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }
}
