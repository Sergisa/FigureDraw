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
}