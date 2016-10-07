package org.sonarsource.bdd;

import java.io.File;
import org.junit.Test;

public class ImageLoaderTest {

  ImageLoader underTest = new ImageLoader();

  @Test
  public void test() throws Exception {
    File filePath = new File(getClass().getClassLoader().getResource("samples").getFile());
    underTest.load(filePath);
  }
}
