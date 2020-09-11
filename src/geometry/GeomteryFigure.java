package geometry;

public abstract class GeomteryFigure implements Figure{
    private double area;
    private double perimetr;
    protected String name;
    public GeomteryFigure(String name){
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;
    }

}
