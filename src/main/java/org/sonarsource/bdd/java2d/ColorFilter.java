package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ColorFilter {

  private float[] referenceColor = {0, 0, 0, 0};
  private float referenceLight;
  private final float minLight;
  private long sampleCount = 0;
  private float threshold = 0.0f;

  public ColorFilter(File[] colorSampleFiles, float minLight) throws IOException {
    this.minLight = minLight * 3;
    long[] avgColor = {0, 0, 0, 0};
    for (File sampleFile : colorSampleFiles) {
      addColor(sampleFile, avgColor);
    }
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
    for (File sampleFile : colorSampleFiles) {
      increaseThreshold(sampleFile);
    }
  }

  private void addColor(File sampleFile, long[] avgColor) throws IOException {
    BufferedImage image = ImageIO.read(sampleFile);
    Raster inRaster = image.getRaster();
    int width = image.getWidth();
    int height = image.getHeight();
    int[] color = new int[4];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (color[3] != 0) {
          avgColor[0] += color[0];
          avgColor[1] += color[1];
          avgColor[2] += color[2];
          sampleCount++;
        }
      }
    }
  }

  private void increaseThreshold(File sampleFile) throws IOException {
    BufferedImage image = ImageIO.read(sampleFile);
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
}
