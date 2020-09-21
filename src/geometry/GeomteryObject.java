package geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GeomteryObject{
    private Color color;

    public GeomteryObject(){}

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }

}
