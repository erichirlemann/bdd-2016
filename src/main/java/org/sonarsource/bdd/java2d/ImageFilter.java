package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageFilter {

  private final ColorFilter colorFilter;

  public ImageFilter(ColorFilter colorFilter) {
    this.colorFilter = colorFilter;
  }

  public static class Result {
    public final BufferedImage image;
    public final long numberOfMatches;

    public Result(BufferedImage image, long numberOfMatches) {
      this.image = image;
      this.numberOfMatches = numberOfMatches;
    }
  }

  public Result apply(BufferedImage image) {
    Raster inRaster = image.getRaster();
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    WritableRaster outRaster = outImage.getRaster();
    int[] badColor = {0x15, 0x51, 0xd3};
    int[] color = new int[3];
    long numberOfMatches = 0;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (colorFilter.matches(color)) {
          numberOfMatches++;
          outRaster.setPixel(i, j, color);
        } else {
          outRaster.setPixel(i, j, badColor);
        }
      }
    }
    return new Result(outImage, numberOfMatches);
  }
}
