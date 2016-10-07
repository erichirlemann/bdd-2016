package org.sonarsource.bdd;

import org.bytedeco.javacv.FrameGrabber;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    try {
      new BackgroundSubtraction().videoMotionDetector();
    } catch (FrameGrabber.Exception e) {
      e.printStackTrace();
    }
  }
}
