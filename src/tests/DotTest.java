package tests;

import geometry.Dot;
import org.junit.Assert;
import org.junit.Test;

public class DotTest {

    @Test
    public void rotate() {
        Dot rotating, expectingDot;
        rotating = new Dot(100,300);
        rotating.rotate(-90, new Dot(100,100));
        expectingDot = new Dot(300, 100);
        Assert.assertEquals("check rotation", rotating.rotate(-90,new Dot(100,100)), expectingDot);
    }

    @Test
    public void testRotate() {
        Dot rotating, expectingDot;
        rotating = new Dot(100,300);
        Dot relationDot = new Dot(100,100);
        expectingDot = new Dot(241.42, 241.42);
        Assert.assertEquals("check rotation", rotating.rotate(-45, relationDot), expectingDot);
    }
}