package geometry;

public class Dot {
    public float x;
    public float y;

    public Dot(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void rotate(double angle){
        rotate(angle, new Dot(0,0));
    }

    public void rotate(double angle, Dot rotationPoint){
        this.x = (float) (((this.x - rotationPoint.x) * Math.cos(angle)) - ((this.y - rotationPoint.y) * Math.sin(angle)));
        this.y = (float) (((this.x - rotationPoint.x) * Math.sin(angle)) + ((this.y - rotationPoint.y) * Math.cos(angle)));
    }
}
