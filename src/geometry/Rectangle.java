package geometry;

import java.util.Arrays;
import java.util.Objects;

public class Rectangle extends GeomteryFigure{
    public float height;
    public float width;
    static String type="Прямоугольник";



    public Rectangle(Dot... dots) {
        super("");
        mainDots.addAll(Arrays.asList(dots));
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
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

    public static final class Builder {

        private String name;
        private DotList mainDots;
        private float height;
        private float width;

        public Builder() {
            mainDots = new DotList();
        }

        public static Builder aRectangle(){
            return new Builder();
        }

        public Builder fromDots(DotList val) {
            mainDots = val;
            return this;
        }

        public Builder withHeight(float val) {
            height = val;
            return this;
        }

        public Builder withWidth(float val) {
            width = val;
            return this;
        }

        public Builder withCenterPoint(Dot center){
            this.mainDots.clear();
            this.mainDots.add(new Dot(center.x-width/2, center.y+height/2));
            this.mainDots.add(new Dot(center.x+width/2, center.y+height/2));
            this.mainDots.add(new Dot(center.x+width/2, center.y-height/2));
            this.mainDots.add(new Dot(center.x-width/2, center.y-height/2));

            return this;
        }
        public Builder withDiagonal(Line diagonal){
            width = Math.abs(diagonal.point1.x - diagonal.point2.x);
            height = Math.abs(diagonal.point1.y - diagonal.point2.y);
            return withCenterPoint(diagonal.getCenter());
        }

        public Builder addPoint(Dot point){
            if(mainDots.size()<4) mainDots.add(point);
            return this;
        }

        public Rectangle build() {
            Rectangle rect = new Rectangle();
            rect.setMainDots(mainDots);
            return rect;
        }
    }
}
