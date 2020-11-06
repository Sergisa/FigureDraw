package tests;

import geometry.Dot;
import geometry.DotList;
import geometry.Line;
import geometry.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class SolverTest {

    @Test
    public void distance() {
    }

    @Test
    public void testDistance() {
    }

    @Test
    public void crosses() {
        Line a = new Line(
                new Dot(1,5),
                new Dot(5,1)
        );
        Line b = new Line(
                new Dot(4,4),
                new Dot(8,8)
        );

        Assert.assertFalse("Check not crossing lines", Solver.crosses(a,b));

        a = new Line(
                new Dot(1,5),
                new Dot(4,1)
        );
        b = new Line(
                new Dot(4,6),
                new Dot(2,2)
        );

        Assert.assertTrue("Check crossing lines", Solver.crosses(a,b));


        a = new Line(
                new Dot(1,5),
                new Dot(4,1)
        );
        b = new Line(
                new Dot(4,1),
                new Dot(4,6)
        );

        Assert.assertTrue("Check crossing chained lines", Solver.crosses(a,b));
    }

    @Test
    public void swap() {
    }

    @Test
    public void doubleArea() {
        Dot[] dotArray = new Dot[]{
                new Dot(3,4),
                new Dot(9,4),
                new Dot(7,1),
                new Dot(1,1)
        };
        DotList dotList = new DotList(Arrays.asList(dotArray));
        Assert.assertEquals("triangle double area", -36, Solver.doubleArea(dotList), 0.1);
    }

    @Test
    public void testArea() {
        Assert.assertEquals("triangle area", 18, Solver.area(
                new Dot(3,4),
                new Dot(9,4),
                new Dot(7,1),
                new Dot(1,1)
        ), 0.1);
    }
}