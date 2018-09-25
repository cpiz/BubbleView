package com.cpiz.android.bubbleview;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
    private static final float DELTA = 0.000001f;
    @Test
    public void testBoundShouldReturn5WhenMinIs1MaxIs10ValIs5() {
        float result = Utils.bound(1, 5, 10);
        Assert.assertEquals(5.0, result, DELTA);
    }

    @Test
    public void testBoundShouldReturn1WhenMinIs1MaxIs10ValIs0() {
        float result = Utils.bound(1, 0, 10);
        Assert.assertEquals(1.0, result, DELTA);
    }

    @Test
    public void testBoundShouldReturn10WhenMinIs1MaxIs10ValIs100() {
        float result = Utils.bound(1, 100, 10);
        Assert.assertEquals(10.0, result, DELTA);
    }
}
