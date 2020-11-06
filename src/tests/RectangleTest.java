package tests;

import geometry.Dot;
import geometry.Line;
import geometry.Rectangle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RectangleTest {
    Rectangle expectingRectangle;

    @Before
    public void buildExpectingTriangle(){
        expectingRectangle = new Rectangle(
                new Dot(1,6),
                new Dot(5,6),
                new Dot(5,1),
                new Dot(1,1)
        );
    }

    @Test
    public void create(){
        Rectangle.Builder mainBuilder = Rectangle.Builder
            .aRectangle()
            .withHeight(5)
            .withWidth(4);


        Rectangle actualRectangle = mainBuilder
                .withDiagonal(new Line(
                    new Dot(5,1),
                    new Dot(1,6)
                )).build();


        Rectangle actualRectangle2 = mainBuilder
                .withDiagonal(new Line(
                    new Dot(1,6),
                    new Dot(5,1)
                )).build();

        Assert.assertEquals("constructor via diagonal ASC and WH",expectingRectangle, actualRectangle);
        Assert.assertEquals("constructor via diagonal DESC and WH",expectingRectangle, actualRectangle2);

        actualRectangle = mainBuilder
                .withCenterPoint(new Dot(3,3.5))
                .build();

        Assert.assertEquals("constructor via center and WH",expectingRectangle, actualRectangle);


    }
}