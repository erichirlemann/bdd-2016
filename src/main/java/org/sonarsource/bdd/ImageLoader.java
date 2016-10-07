package org.sonarsource.bdd;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;

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
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir.toPath())) {
      IplImage ref = null;
      for (Path entry : stream) {
        if (ref == null) {
          ref = cvLoadImage(entry.toFile().getAbsolutePath());
          crop(ref);
          continue;
        }
        int nb = processImage(ref, entry.toFile());
        System.out.println(entry.getFileName() + " : " + nb);
      }
    }
  }

  private int processImage(IplImage ref, File file) {
    IplImage image = cvLoadImage(file.getAbsolutePath());
    crop(image);

    IplImage cropped = cvCreateImage(cvGetSize(image), image.depth(), image.nChannels());
    cvCopy(image, cropped);

    int nb = beeDetector.detect(ref, image, file.getName());

    cvReleaseImage(image);
    cvReleaseImage(cropped);

    return nb;
  }

  private void crop(IplImage image) {
    CvRect r = new CvRect(80, 80, 2000, 2000);
    cvSetImageROI(image, r);
  }

  private void save(IplImage frame, String name) {
    File outputFile = new File(outputFolder, name);
    cvSaveImage(outputFile.getAbsolutePath(), frame);
  }
}
