package tests;

import geometry.Dot;
import geometry.Line;
import geometry.Rectangle;
import org.junit.Assert;
import org.junit.Test;

public class RectangleTest {

    @Test
    public void create(){
        Rectangle expectingRectangle = new Rectangle(
                new Dot(1,6),
                new Dot(5,6),
                new Dot(5,1),
                new Dot(1,1)
        );
        Rectangle actualRectangle = new Rectangle(
                new Line(new Dot(1,6), new Dot(5,1)),
                4,
                5
        );
        Rectangle actualRectangle2 = new Rectangle(
                new Line(new Dot(1,6), new Dot(5,1)),
                4,
                5
        );

        Assert.assertEquals("constructor via diagonal ASC and WH",expectingRectangle, actualRectangle);
        Assert.assertEquals("constructor via diagonal DESC and WH",expectingRectangle, actualRectangle2);

        actualRectangle = new Rectangle(
                5,
                4,
                new Dot(3,3.5)
        );

        Assert.assertEquals("constructor via center and WH",expectingRectangle, actualRectangle);

        actualRectangle = new Rectangle(
                new Dot(1,6),
                new Dot(5,1),
                4,
                5
        );
        actualRectangle2 = new Rectangle(
                new Dot(5,1),
                new Dot(1,6),
                4,
                5
        );

        Assert.assertEquals("constructor via two diagonal points ASC and WH", expectingRectangle, actualRectangle);
        Assert.assertEquals("constructor via two diagonal points DESC and WH", expectingRectangle, actualRectangle2);

    }
}