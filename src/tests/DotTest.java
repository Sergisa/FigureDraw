package tests;

import geometry.Dot;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DotTest {

    @Test
    public void rotate() {
        Dot rotating, expectingDot;
        rotating = new Dot(100,300);
        rotating.rotate(-90, new Dot(100,100));
        expectingDot = new Dot(300, 100);
        Assert.assertEquals("check same", rotating, expectingDot);
    }

    @Test
    public void testRotate() {
        Dot rotating, expectingDot;
        rotating = new Dot(100,300);
        rotating.rotate(-45, new Dot(100,100));

        expectingDot = new Dot((float) 241.42, (float) 241.42);

        Assert.assertEquals("check x", rotating, expectingDot);
    }
}