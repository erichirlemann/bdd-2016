package org.sonarsource.bdd;

import org.opencv.core.Core;

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    new BackgroundSubtraction().execute();
  }
}
