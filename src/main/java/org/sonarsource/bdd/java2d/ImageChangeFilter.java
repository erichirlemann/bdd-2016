package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageChangeFilter {

  private final ColorFilter colorFilter;

  public ImageChangeFilter(ColorFilter colorFilter) {
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

  public ImageFilter.Result apply(BufferedImage previousImage, BufferedImage image) {
    Raster inPrevRaster = previousImage.getRaster();
    Raster inRaster = image.getRaster();
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    WritableRaster outRaster = outImage.getRaster();
    int[] badColor = {0x15, 0x51, 0xd3};
    int[] color = new int[3];
    long numberOfMatches = 0;
    int margin = 6;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (colorFilter.matches(color) &&
          i >= margin && i + margin < width && j >= margin && j + margin < height &&
          !matchAround(inPrevRaster, i, j, color, margin, 40)) {
          numberOfMatches++;
          outRaster.setPixel(i, j, color);
        } else {
          outRaster.setPixel(i, j, badColor);
        }
      }
    }
    return new ImageFilter.Result(outImage, numberOfMatches);
  }

  private boolean matchAround(Raster inPrevRaster, int centerI, int centerJ, int[] color, int margin, int threshold) {
    int[] prevColor = new int[3];
    for (int i = centerI - margin; i < centerI + margin; i++) {
      for (int j = centerJ - margin; j < centerJ + margin; j++) {
        prevColor = inPrevRaster.getPixel(i, j, prevColor);
        if (Math.abs(color[0] - prevColor[0]) + Math.abs(color[1] - prevColor[1]) + Math.abs(color[2] - prevColor[2]) < threshold) {
          return true;
        }
      }
    }
    return false;
  }

}
