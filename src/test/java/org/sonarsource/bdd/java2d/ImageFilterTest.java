package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Before;
import org.junit.Test;

public class ImageFilterTest {

  private ImageFilter imageFilter;

  @Before
  public void before() throws IOException {
    imageFilter = new ImageFilter(new ColorFilter(new File[] {ImageSamples.POLLEN_FILE2}, 40));
  }

  @Test
  public void testFile1() throws IOException {
      convert(ImageSamples.IMG_FILE1, "img1");
  }

  @Test
  public void testFile2() throws IOException {
      convert(ImageSamples.IMG_FILE2, "img2");
  }

  @Test
  public void testFile3() throws IOException {
      convert(ImageSamples.IMG_FILE3, "img3");
  }

    private void convert(File inputPath, String outputName) throws IOException {
        BufferedImage image = ImageIO.read(inputPath);
        ImageFilter.Result result = imageFilter.apply(image);
        System.out.println("Pollen Pixels = " + result.numberOfMatches);
        ImageIO.write(image, "png", ImageSamples.resultFile(this, outputName +".1.png"));
        ImageIO.write(result.image, "png", ImageSamples.resultFile(this, outputName +".2.png"));
    }

}
