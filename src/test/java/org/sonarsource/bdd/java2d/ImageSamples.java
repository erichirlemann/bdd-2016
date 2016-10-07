package org.sonarsource.bdd.java2d;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageSamples {

  public static final String DIR = "src/test/resources/samples/";

  public static final File IMG_FILE1 = new File(DIR + "DSC_0218-min.JPG");
  public static final File IMG_FILE2 = new File(DIR + "DSC_0219-min.JPG");
  public static final File IMG_FILE3 = new File(DIR + "DSC_0220-min.JPG");

  public static final File POLLEN_FILE1 = new File(DIR + "pollen1.png");
  public static final File POLLEN_FILE2 = new File(DIR + "pollen2.png");

  public static final File[] IMG_FILES = {IMG_FILE1, IMG_FILE2, IMG_FILE3};

  public static File resultFile(Object testObject, String fileName) throws IOException {
    Path baseDir = Paths.get("target/test-output").resolve(testObject.getClass().getSimpleName());
    Files.createDirectories(baseDir);
    Path targetFile = baseDir.resolve(fileName);
    if (Files.exists(targetFile)) {
      Files.delete(targetFile);
    }
    return targetFile.toFile();
  }

}
