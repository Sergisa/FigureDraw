import geometry.*;
import processing.core.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    Dot rotationRelativeDot, rotatingDot, rotatedDot;
    Drawer drawer;
    List<GeomteryFigure> figures;
    Line line1;
    Line line2;
    public void settings()
    {
        this.drawer = new Drawer(this);
        figures = new ArrayList<>();
        rotationRelativeDot = new Dot(100,100);
        rotationRelativeDot.setColor(Color.RED);
        rotatingDot = new Dot(100,300);
        rotatingDot.setColor(Color.BLACK);
        rotatedDot = new Dot(100,300);
        rotatedDot.setColor(Color.BLUE);
        rotatedDot.rotate(-45, rotationRelativeDot);
        line1 = Line.create(rotationRelativeDot, rotatingDot);
        line1.setColor(Color.CYAN);
        line2 = Line.create(rotationRelativeDot, rotatedDot);
        line2.setColor(Color.CYAN);


        System.out.println("Rotating point  x: "+ rotatingDot.x + "  y: "+ rotatingDot.y);
        System.out.println("Relation point  x: "+ rotationRelativeDot.x + "  y: "+ rotationRelativeDot.y);
        size(500,500);
        for (GeomteryFigure figure:figures) {
            System.out.println(figure.getClass().getSuperclass().getName() +" ["+ figure.getClass().getName()+"] "+figure.getName());
        }
    }

    public void setup(){
        //size(500,500);
    }

    public void mousePressed()
    {
        System.out.println("Mouse pressed");
    }

    public void mouseReleased()
    {
        System.out.println("Mouse released");
    }

    public void keyPressed()
    {
        System.out.println("key pressed");
    }

    public void draw()
    {
        background(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue());
        strokeWeight(4);
        drawer.draw(line1);
        drawer.draw(line2);
        drawer.draw(rotatingDot);
        drawer.draw(rotatedDot);
        //stroke(204, 102, 0);
        drawer.draw(rotationRelativeDot);
        super.draw();
    }


    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "DueAWGControllerLinux" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}
