import processing.core.*;

public class Drawer {
    private PApplet applet;
    public Drawer(PApplet applet){
        this.applet = applet;
    }
    public void draw(GeomteryFigure figure){
        if(figure instanceof Rectangle){
            this.applet.rect(0,2,5,8,2);
        }
    }
}
