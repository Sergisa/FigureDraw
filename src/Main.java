import geometry.*;
import processing.core.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Main {
    private static Dot rotationRelativeDot, rotatingDot, rotatedDot;
    private static List<GeomteryFigure> figures;
    private static Line line1;
    private static Line line2;

    static public void main(String[] passedArgs) {
        //Drawer drawer = new Drawer(this);
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
        //size(500,500);
        for (GeomteryFigure figure:figures) {
            System.out.println(figure.getClass().getSuperclass().getName() +" ["+ figure.getClass().getName()+"] "+figure.getName());
        }
        form frm = new form();
    }
}
