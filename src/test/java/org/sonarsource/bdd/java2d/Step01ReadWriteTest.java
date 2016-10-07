package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class Step01ReadWriteTest {

  @Test
  public void test() throws IOException {
    File srcFile = ImageSamples.IMG_FILE1;
    File dstFile = ImageSamples.resultFile(this, "res.jpg");

    BufferedImage image = ImageIO.read(srcFile);
    ImageIO.write(image, "jpg", dstFile);
  }

}
