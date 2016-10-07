package org.sonarsource.bdd;

import java.io.File;

public class ImageMotionDetection {

  public void execute(){
    File filePath = new File(getClass().getResource("/resources/samples").getFile());
    File file1 = new File(filePath, "DSC_0218-min.JPG");
    File file2 = new File(filePath, "DSC_0219-min.JPG");
    File file3 = new File(filePath, "DSC_0220-min.JPG");

    if (!file1.exists()) {
      throw new IllegalArgumentException("File not found");
    }

  }

}
