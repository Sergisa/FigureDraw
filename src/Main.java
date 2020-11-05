import geometry.*;

import java.util.List;
import java.util.function.Consumer;

public class Main {
    private static Dot rotationRelativeDot, rotatingDot, rotatedDot;
    private static List<GeomteryFigure> figures;
    private static Line line1;
    private static Line line2;


    static public void main(String[] passedArgs) {

        Triangle tri = new Triangle(
                new Dot(7,1),
                new Dot(2,1),
                new Dot(2,6)
        );
        tri.getPairs().forEach(new Consumer<DotPair>() {
            @Override
            public void accept(DotPair dotDotDotPair) {
                System.out.println("Pair: "+ dotDotDotPair);
            }
        });
        System.out.println("Perimeter : " + tri.getPerimeter());
        System.out.println("Area: "+tri.getArea());
    }
}
