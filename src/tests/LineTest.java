package tests;

import geometry.Dot;
import geometry.Line;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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

        Assert.assertEquals(expectingLine, actualLine);
    }
}