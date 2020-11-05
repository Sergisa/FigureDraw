package geometry;

public class DotPair extends Pair{

    public DotPair(final Dot l, final Dot r) {
        super(l,r);
    }

    public double getDistance(){
        return Solver.distance((Dot)left, (Dot)right);
    }
}