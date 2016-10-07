package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class Step04LoadColorTest {

  @Test
  public void test() throws IOException {
    File srcFile = ImageSamples.POLLEN_FILE2;
    BufferedImage image = ImageIO.read(srcFile);
    System.out.println(image);
    Raster inRaster = image.getRaster();

    int width = image.getWidth();
    int height = image.getHeight();

    int count = 0;
    int[] color = new int[4];
    long[] avg_color = {0, 0, 0, 0};

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        color = inRaster.getPixel(i, j, color);
        if (color[3] != 0) {
          avg_color[0] += color[0];
          avg_color[1] += color[1];
          avg_color[2] += color[2];
          count++;
        }
      }
    }
    System.out.println("pollenPixels = " + count);
    long r = avg_color[0] / count;
    long g = avg_color[1] / count;
    long b = avg_color[2] / count;

    long max = Math.max(1, Math.max(r, Math.max(g, b)));
    double scale = 255.0 / max;
    System.out.println("R = " + Math.round(r * scale));
    System.out.println("G = " + Math.round(g * scale));
    System.out.println("B = " + Math.round(b * scale));
  }

}
