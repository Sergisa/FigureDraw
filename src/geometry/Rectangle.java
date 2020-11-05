package geometry;

import java.util.Arrays;
import java.util.Objects;

public class Rectangle extends GeomteryFigure{
    public float height;
    public float width;
    static String type="Прямоугольник";

    public Rectangle(float height, float width, Dot center) {
        this(
                new Dot(center.x-width/2, center.y+height/2),
                new Dot(center.x+width/2, center.y+height/2),
                new Dot(center.x+width/2, center.y-height/2),
                new Dot(center.x-width/2, center.y-height/2)
        );
    }

    public Rectangle(Line diagonal, float width, float height) {
        this(
                height,
                width,
                diagonal.getCenter()
        );
    }

    public Rectangle(Dot diagonalDot1, Dot diagonalDot2, float width, float height) {
        this(
                new Line(diagonalDot1, diagonalDot2),
                width,
                height
        );
    }

    public Rectangle(Dot... dots) {
        super("");
        mainDots.addAll(Arrays.asList(dots));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rectangle)) return false;
        Rectangle rectangle = (Rectangle) o;
        return mainDots.equals(rectangle.mainDots);
        /*return Float.compare(rectangle.height, height) == 0 &&
                Float.compare(rectangle.width, width) == 0;*/
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "height=" + height +
                ", width=" + width +
                ", mainDots=" + mainDots +
                "} " + super.toString();
    }
}
