package org.sonarsource.bdd;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

public class ImageLoader {

  private final BeeDetector beeDetector = new BeeDetector();

  private File outputFolder = new File("target/" + getClass().getSimpleName());

  public ImageLoader() {
    try {
      FileUtils.deleteQuietly(outputFolder);
      FileUtils.forceMkdir(outputFolder);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void load(File inputDir) throws IOException {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir.toPath(), "*.JPG")) {
      IplImage ref = null;
      for (Path entry : stream) {
        if (ref == null) {
          ref = cvLoadImage(entry.toFile().getAbsolutePath());
          crop(ref);
          save(ref, entry.toFile().getName() + "-cropped.jpg");
          continue;
        }
        int nb = processImage(ref, entry.toFile());
        System.out.println(entry.getFileName() + " : " + nb);
      }
    }
  }

  private int processImage(IplImage ref, File file) {
    IplImage image = cvLoadImage(file.getAbsolutePath());

    IplImage cropped = crop(image);
    save(cropped, file.getName() + "-cropped.jpg");

    int nb = beeDetector.detect(ref, cropped, file.getName());

    cvReleaseImage(image);
    cvReleaseImage(cropped);
    return nb;
  }

  private IplImage crop(IplImage image) {
    CvRect r = new CvRect(1600, 1500, 2000, 1000);
    cvSetImageROI(image, r);
    return image;
  }

  private void save(IplImage frame, String name) {
    File outputFile = new File(outputFolder, name);
    //cvSaveImage(outputFile.getAbsolutePath(), frame);
  }
}
