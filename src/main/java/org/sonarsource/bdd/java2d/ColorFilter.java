package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ColorFilter {

  private float[] referenceColor = {0, 0, 0, 0};
  private float referenceLight;
  private float minLight;
  private long sampleCount = 0;
  private float threshold = 0.0f;
  private int[] min = {255, 255, 255};
  private int[] max = {0, 0, 0};

  public static final ColorFilter getPollenInstance() {
    return Holder.POLLEN;
  }

  private static class Holder {
    private static final ColorFilter POLLEN = new ColorFilter(ColorFilter.class.getResource("/pollen.png"), 80);
  }

  public ColorFilter(URL referenceImage, float minLight) {
    try {
      this.minLight = 0;
      long[] avgColor = {0, 0, 0, 0};
      addColor(referenceImage, avgColor);
      if (sampleCount > 0) {
        long max = Math.max(1, Math.max(avgColor[0], Math.max(avgColor[1], avgColor[2]))) / sampleCount;
        double scale = 255.0 / max;
        for (int c = 0; c < 3; c++) {
          referenceColor[c] = (float) (avgColor[c] / sampleCount * scale);
        }
      }
      referenceLight = 0;
      for (int c = 0; c < 3; c++) {
        referenceLight += referenceColor[c];
      }
      increaseThreshold(referenceImage);
      this.minLight = minLight * 3;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addColor(URL referenceImage, long[] avgColor) throws IOException {
    BufferedImage image = ImageIO.read(referenceImage);
    Raster inRaster = image.getRaster();
    int width = image.getWidth();
    int height = image.getHeight();
    int[] color = new int[4];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (color[3] != 0) {
          for (int c = 0; c < 3; c++) {
            avgColor[c] += color[c];
            min[c] = Math.min(min[c], color[c]);
            max[c] = Math.max(max[c], color[c]);
          }
          sampleCount++;
        }
      }
    }
  }

  private void increaseThreshold(URL referenceImage) throws IOException {
    BufferedImage image = ImageIO.read(referenceImage);
    Raster inRaster = image.getRaster();
    int width = image.getWidth();
    int height = image.getHeight();
    int[] color = new int[4];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (color[3] != 0) {
          threshold = Math.max(threshold, distance(color));
        }
      }
    }
  }

  public boolean matches(int[] color) {
    return distance(color) <= threshold;
  }

  public float distance(int[] color) {
    float light = 0;
    for (int c = 0; c < 3; c++) {
      light += color[c];
    }
    if (light <= minLight) {
      return 1.0f;
    }
    double scale = referenceLight / light;
    float distance = 0;
    for (int c = 0; c < 3; c++) {
      distance += Math.abs(referenceColor[c] - (float) (color[c] * scale));
    }
    return distance / (255 * 3);
  }

  public float getR() {
    return referenceColor[0];
  }

  public float getG() {
    return referenceColor[1];
  }

  public float getB() {
    return referenceColor[2];
  }

  public long getSampleCount() {
    return sampleCount;
  }

  public float getThreshold() {
    return threshold;
  }

  public int[] getMin() {
    return min;
  }

  public int[] getMax() {
    return max;
  }
}
