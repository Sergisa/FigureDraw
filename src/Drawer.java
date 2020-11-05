import geometry.*;
import processing.core.PApplet;

public class Drawer {
    final private PApplet applet;
    final private int[] startPoint = {3,2};

    public Drawer(PApplet applet){
        this.applet = applet;
    }

    public void draw(GeomteryObject object){
        if(object instanceof Line){
            this.applet.strokeWeight(1);

            if (((Line)object).getColor() != null){
                this.applet.stroke(
                        ((Line)object).getColor().getRed(),
                        ((Line)object).getColor().getGreen(),
                        ((Line)object).getColor().getBlue()
                );
            }
            this.applet.line(
                    ((Line)object).point1.x,
                    ((Line)object).point1.y,
                    ((Line)object).point2.x,
                    ((Line)object).point2.y
            );
        }
        if(object instanceof Dot){
            this.applet.strokeWeight(4);
            if (object.getColor() != null){
                this.applet.stroke(
                        object.getColor().getRed(),
                        object.getColor().getGreen(),
                        object.getColor().getBlue()
                );
            }
            this.applet.point(((Dot)object).x, ((Dot)object).y);
        }
    }
    public void draw(GeomteryFigure figure){
        final int X_POINTER = 1;
        final int Y_POINTER = 2;

        if (figure.getColor() != null){
            this.applet.stroke(figure.getColor().getRed(),figure.getColor().getGreen(), figure.getColor().getBlue());
        }
        if(figure instanceof Rectangle){
            //this.applet.rectMode(PConstants.CENTER);
            this.applet.rect(
                    startPoint[X_POINTER],
                    startPoint[Y_POINTER],
                    ((Rectangle) figure).height,
                    ((Rectangle) figure).width
            );
        }

        /*if(figure instanceof Triangle){
            //this.applet.triangle();
        }*/
        if(figure instanceof Ellipse){
            this.applet.arc(
                    ((Ellipse) figure).getCenter().x,
                    ((Ellipse) figure).getCenter().y,
                    ((Ellipse) figure).radius1 * 2,
                    ((Ellipse) figure).radius2 * 2,
                    0,
                    7
            );
        }
    }
}
