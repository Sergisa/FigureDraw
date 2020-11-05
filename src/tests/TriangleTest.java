package tests;

import geometry.Dot;
import geometry.Triangle;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TriangleTest {
    @Test
    public void create(){
        Triangle expecting = new Triangle(
                new Dot(1,1),
                new Dot(1,5),
                new Dot(4,1)
        );

        Assert.assertEquals("check length",4, expecting.a, 0);
        Assert.assertEquals("check length",5, expecting.b, 0);
        Assert.assertEquals("check length",3, expecting.c, 0);
    }
}