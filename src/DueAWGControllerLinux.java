import algebra.Algoritm;
import algebra.LinearSystem;
import algebra.MyEquation;
import geometry.Circle;
import geometry.GeomteryFigure;
import geometry.Rectangle;
import geometry.Triangle;
import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class DueAWGControllerLinux extends PApplet {
    Drawer drawer;
    Rectangle myRectangle;
    Circle myCircle;
    Triangle myTriangle;
    List<GeomteryFigure> figures;
    private static final int DEFAULT_EQUATIONS_NUMBER = 3;
    private static final int DEFAULT_VARIABLES_NUMBER = 3;
    public void settings()
    {
        this.drawer = new Drawer(this);
        myRectangle = new Rectangle(2,5, "Мой прямоугольник");
        myCircle = new Circle(5);
        myTriangle = new Triangle(3,2,4);
        figures = new ArrayList<GeomteryFigure>();
        figures.add(myRectangle);
        figures.add(myCircle);
        figures.add(myTriangle);
        size(500,500);
        System.out.println( myTriangle.getArea() );
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
        drawer.draw(new Circle(50));
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
