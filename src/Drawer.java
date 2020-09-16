import geometry.Circle;
import geometry.GeomteryFigure;
import geometry.Rectangle;
import geometry.Triangle;
import processing.core.*;

public class Drawer {
    final private PApplet applet;
    final private int[] startPoint = {3,2};
    final private int X_POINTER = 1;
    final private int Y_POINTER = 2;
    public Drawer(PApplet applet){
        this.applet = applet;
    }
    public void draw(GeomteryFigure figure){
        if(figure instanceof Rectangle){
            //this.applet.rectMode(PConstants.CENTER);
            this.applet.rect(
                    startPoint[X_POINTER],
                    startPoint[Y_POINTER],
                    ((Rectangle) figure).height,
                    ((Rectangle) figure).width
            );
        }

        if(figure instanceof Triangle){
            //this.applet.triangle();
        }
        if(figure instanceof Circle){
            this.applet.arc(
                    35,
                    35,
                    ((Circle) figure).radius,
                    ((Circle) figure).radius,
                    0,
                    1000
            );
        }
    }
}
