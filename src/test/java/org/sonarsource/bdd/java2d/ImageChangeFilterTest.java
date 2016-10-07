package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageChangeFilterTest {

  private static ColorFilter colorFilter = ColorFilter.getPollenInstance();

  @Test
  public void testFile1() throws IOException {
    convert(ImageSamples.IMG_FILE3, ImageSamples.IMG_FILE1, "img1");
  }

  @Test
  public void testFile2() throws IOException {
    convert(ImageSamples.IMG_FILE1, ImageSamples.IMG_FILE2, "img2");
  }

  @Test
  public void testFile3() throws IOException {
    convert(ImageSamples.IMG_FILE2, ImageSamples.IMG_FILE3, "img3");
  }

  private void convert(File previousImagePath, File imagePath, String outputName) throws IOException {
    BufferedImage previousImage = ImageIO.read(previousImagePath);
    BufferedImage image = ImageIO.read(imagePath);
    ImageChangeFilter filter = new ImageChangeFilter(colorFilter);
    ImageFilter.Result result = filter.apply(previousImage, image);
    System.out.println("Pollen Pixels = " + result.numberOfMatches);
    ImageIO.write(image, "png", ImageSamples.resultFile(this, outputName + ".1.png"));
    ImageIO.write(result.image, "png", ImageSamples.resultFile(this, outputName + ".2.png"));
  }

}
