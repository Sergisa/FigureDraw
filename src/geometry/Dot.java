package geometry;

import java.awt.*;
import java.util.Objects;

public class Dot extends GeomteryObject{
    public float x;
    public float y;
    private Color color;
    public Dot(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void rotate(double angle){
        this.rotate(angle, new Dot(0,0));
    }

    public void rotate(double angle, Dot rotationPoint){
        angle = angle * (Math.PI / 180);
        float newX,newY;
        newX = (float) (((this.x - rotationPoint.x) * Math.cos(angle)) - ((this.y - rotationPoint.y) * Math.sin(angle)) + rotationPoint.x);
        newY = (float) (((this.x - rotationPoint.x) * Math.sin(angle)) + ((this.y - rotationPoint.y) * Math.cos(angle)) + rotationPoint.y);
        this.x = newX;
        this.y = newY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Dot{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dot)) return false;
        Dot dot = (Dot) o;
        return Float.compare(dot.x, x) < 0.1 &&
                Float.compare(dot.y, y) < 0.1 &&
                Objects.equals(getColor(), dot.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, getColor());
    }
}
