package org.sonarsource.bdd.java2d;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ColorFilterTest {

  @Test
  public void testInitialization() throws IOException {
    ColorFilter colorFilter = ColorFilter.getPollenInstance();
    Assert.assertEquals("SampleCount", 4814, colorFilter.getSampleCount());
    Assert.assertEquals("Threshold", 0.16, colorFilter.getThreshold(), 0.01);
    Assert.assertEquals("R", 255.0, colorFilter.getR(), 0.1);
    Assert.assertEquals("G", 189.14835, colorFilter.getG(), 0.1);
    Assert.assertEquals("B", 35.027473, colorFilter.getB(), 0.1);

    int[] min = colorFilter.getMin();
    int[] max = colorFilter.getMax();
    System.out.println("Min : " + min[0] + ", " + min[1] + ", " + min[2]);
    System.out.println("Max : " + max[0] + ", " + max[1] + ", " + max[2]);
  }

}
