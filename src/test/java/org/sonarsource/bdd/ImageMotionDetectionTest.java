package org.sonarsource.bdd;

import org.junit.Test;

public class ImageMotionDetectionTest {

  ImageMotionDetection underTest = new ImageMotionDetection();

  @Test
  public void test() throws Exception {
    underTest.execute();
  }
}
