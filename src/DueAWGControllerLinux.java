import geometry.*;
import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class DueAWGControllerLinux extends PApplet {
    Drawer drawer;
    Ellipse myCircle;
    List<GeomteryFigure> figures;
    public void settings()
    {
        this.drawer = new Drawer(this);
        figures = new ArrayList<GeomteryFigure>();
        myCircle = Ellipse.createCircle(50);
        myCircle.setName("Кружочек");
        myCircle.setCenter(new Dot(100,100));
        myCircle.rotate(90, new Dot(100, 300));

        figures.add(myCircle);
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
