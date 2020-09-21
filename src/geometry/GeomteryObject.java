package geometry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GeomteryObject implements Figure{
    private Color color;

    protected String name;
    public GeomteryObject(String name){
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }

}
