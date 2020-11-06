package tests;

import geometry.Dot;
import geometry.Line;
import org.junit.Assert;
import org.junit.Test;

public class LineTest {

    @Test
    public void create() {
        Line expectingLine = Line.create(
                new Dot(100,300),
                new Dot(300,100)
        );
        Line actualLine = Line.create(
                new Dot(100,300),
                new Dot(300,100)
        );

        Assert.assertEquals("static builder via Dots",expectingLine, actualLine);
        actualLine = Line.create(new float[]{100,300}, new float[]{300,100});

        Assert.assertEquals("static builder via arrays", expectingLine, actualLine);
    }

    @Test
    public void testCreate() {
    }

    @Test
    public void testCreate1() {
    }

    @Test
    public void getCenter() {
        Dot expectingCenter = new Dot(3,3.5);
        Line actulaLine = new Line(
                new Dot(1,6),
                new Dot(5,1)
        );
        Assert.assertEquals("check center calculation",actulaLine.getCenter(), expectingCenter);
        Assert.assertEquals("check center calculation",actulaLine.getCenter().x, 3, 0);
        Assert.assertEquals("check center calculation",actulaLine.getCenter().y, 3.5,0);
    }
}