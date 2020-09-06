import processing.core.*;

public class DueAWGControllerLinux extends PApplet {
    Drawer drawer;
    public void settings()
    {
        this.drawer = new Drawer(this);
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
        drawer.draw(new Rectangle(2,8));
        drawGrid();
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
