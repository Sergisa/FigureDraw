import geometry.*;
import processing.core.PApplet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Main extends PApplet {
    private static Dot rotationRelativeDot, rotatingDot, rotatedDot;
    private static List<GeomteryFigure> figures;
    private static Line line1;
    private static Line line2;

    public void settings()
    {
        size(500,500);
    }

    public void setup(){
        //size(500,500);
    }

    void drawGrid()
    {
        stroke( 225 );
        for ( int i = 0; i < 64; i++ ) {
            line(i * 10, 0, i * 10, height );
        }
        for ( int i = 0; i < 48; i++ ) {
            line( 0, i * 10, width, i * 10 );
        }
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
        drawGrid();
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "Main" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
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
        System.out.println("Угол поворота: 45 (по часовой стрелке)");
        System.out.println("Rotated point  x: "+ rotatedDot.x + "  y: "+ rotatedDot.y);
        //size(500,500);
        for(GeomteryFigure figure:figures) {
            System.out.println(figure.getClass().getSuperclass().getName() +" ["+ figure.getClass().getName()+"] "+figure.getName());
        }
    }
}
