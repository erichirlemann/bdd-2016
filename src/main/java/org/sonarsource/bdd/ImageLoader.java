package org.sonarsource.bdd;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

public class ImageLoader {

  private static final int FILES_GROUP = 20;

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
      List<File> files = new ArrayList<>();
      for (Path entry : stream) {
        files.add(entry.toFile());
        if (files.size() == FILES_GROUP) {
          // TODO get reference from images average
          processFiles(files.get(0), files);
          files.clear();
        }
      }
      if (!files.isEmpty()) {
        processFiles(files.get(0), files);
      }
    }
  }

  private void processFiles(File fileRef, List<File> files) {
    IplImage ref = cvLoadImage(fileRef.getAbsolutePath());
    crop(ref);
    save(ref, fileRef.getName() + "-ref-cropped.jpg");
    for (File file : files) {
      int nb = processImage(ref, file);
      System.out.println(file.getName() + " : " + nb);
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
    // cvSaveImage(outputFile.getAbsolutePath(), frame);
  }
}
