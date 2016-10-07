package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;

public class ColorFilterTest {

  @Test
  public void testInitialization() throws IOException {
    ColorFilter colorFilter = new ColorFilter(new File[] {ImageSamples.POLLEN_FILE2}, 40);
    Assert.assertEquals("SampleCount", 4814, colorFilter.getSampleCount());
    Assert.assertEquals("Threshold", 0.16, colorFilter.getThreshold(), 0.01);
    Assert.assertEquals("R", 255.0, colorFilter.getR(), 0.1);
    Assert.assertEquals("G", 189.14835, colorFilter.getG(), 0.1);
    Assert.assertEquals("B", 35.027473, colorFilter.getB(), 0.1);
  }

}
