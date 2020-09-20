import geometry.*;
import processing.core.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DueAWGControllerLinux extends PApplet {
    Dot rotationDot;
    Drawer drawer;
    Ellipse myCircle, rotatedCircle;
    List<GeomteryFigure> figures;
    public void settings()
    {
        this.drawer = new Drawer(this);
        figures = new ArrayList<>();
        rotationDot = new Dot(100, 300);
        myCircle = Ellipse.createCircle(30);
        rotatedCircle = Ellipse.createCircle(30);
        rotatedCircle.setColor(Color.BLUE);

        myCircle.setCenter(new Dot(100,100));
        rotatedCircle.setCenter(new Dot(100,100));
        rotatedCircle.rotate(90, rotationDot);
        System.out.println("x: "+rotatedCircle.getCenter().x + "  y: "+rotatedCircle.getCenter().y);
        System.out.println("color: "+rotatedCircle.getColor());
        figures.add(myCircle);
        figures.add(rotatedCircle);
        size(500,500);
        for (GeomteryFigure figure:figures) {
            System.out.println(figure.getClass().getSuperclass().getName() +" ["+ figure.getClass().getName()+"] "+figure.getName());
        }
    }

    public void setup(){
        //size(500,500);
    }

    public void mousePressed()
    {}

    public void mouseReleased()
    {}

    public void keyPressed()
    {
        loop();
    }

    public void draw()
    {
        drawer.draw(myCircle);
        drawer.draw(rotatedCircle);
        strokeWeight(4);
        stroke(204, 102, 0);
        drawer.draw(rotationDot);
        super.draw();
    }


    static public void main(String[] passedArgs) {
        System.out.println("Known figures:");
        String[] appletArgs = new String[] { "DueAWGControllerLinux" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
