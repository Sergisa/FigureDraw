import processing.core.*;

public class Drawer {
    final private PApplet applet;
    public Drawer(PApplet applet){
        this.applet = applet;
    }
    public void draw(GeomteryFigure figure){
        if(figure instanceof Rectangle){
            this.applet.rect(((Rectangle) figure).height,
                    ((Rectangle) figure).width);
        }
    }
}
