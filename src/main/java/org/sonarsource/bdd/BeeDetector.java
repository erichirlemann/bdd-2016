package org.sonarsource.bdd;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.sonarsource.bdd.java2d.ColorFilter;
import org.sonarsource.bdd.java2d.ImageFilter;

import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvAnd;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BLUR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_TREE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvMinAreaRect2;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

public class BeeDetector {

  private static final CvScalar min = cvScalar(0x09, 0x63, 0x90, 0);// BGR-A
  private static final CvScalar max = cvScalar(0x2c, 0xB3, 0xe4, 0);// BGR-A

  private File outputFolder = new File("target/" + getClass().getSimpleName());

  private static final OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
  private static final Java2DFrameConverter paintConverter = new Java2DFrameConverter();

  private final ImageFilter imageFilter = new ImageFilter(ColorFilter.getPollenInstance());

  public BeeDetector() {
    try {
      FileUtils.deleteQuietly(outputFolder);
      FileUtils.forceMkdir(outputFolder);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public int detect(IplImage ref, IplImage image, String fileName) {
    IplImage diff = diff(ref, image);
    // do some threshold for wipe away useless details
    cvThreshold(diff, diff, 100, 255, CV_THRESH_BINARY);
    cvSmooth(diff, diff, CV_BLUR, 9, 9, 2, 2);
    // save(diff, fileName + "-diff");

    IplImage mask = cvCreateImage(cvGetSize(diff), IPL_DEPTH_8U, 1);
    cvCvtColor(diff, mask, CV_RGB2GRAY);
    // save(mask, fileName + "-mask");

    IplImage copyAnd = copyEmpty(image);
    cvAnd(image, image, copyAnd, mask);
    // save(copyAnd, fileName + "-and");

    BufferedImage bufferedImage = toBufferedImage(copyAnd);
    //IplImage filteredImage = toIplImage(imageFilter.apply(bufferedImage).image);
    IplImage filteredImage = toIplImage(bufferedImage);

    IplImage result = cvCreateImage(cvGetSize(ref), IPL_DEPTH_8U, 1);
    cvInRangeS(filteredImage, min, max, result);

    int nbCount = contours(result, fileName);

    cvReleaseImage(diff);
    cvReleaseImage(mask);
    cvReleaseImage(copyAnd);
    cvReleaseImage(result);
    cvReleaseImage(filteredImage);

    return nbCount;
  }

  private IplImage diff(IplImage frame, IplImage frame2) {
    IplImage diff = copyEmpty(frame);
    cvAbsDiff(frame, frame2, diff);
    return diff;
  }

  private int contours(IplImage image, String fileName) {
    IplImage contourImage = image.clone();
    cvSmooth(contourImage, contourImage, CV_BLUR, 30, 9, 2, 2);

    opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();
    opencv_core.CvSeq contour = new opencv_core.CvSeq(null);
    cvFindContours(contourImage, storage, contour, Loader.sizeof(opencv_core.CvContour.class), CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);

    int nbCont = 0;
    while (contour != null && !contour.isNull()) {
      if (contour.elem_size() > 0) {
        opencv_core.CvBox2D box = cvMinAreaRect2(contour, storage);
        // test intersection
        if (box != null) {
          opencv_core.CvSize2D32f size = box.size();
          if (size.width() * size.height() > 1000) {
            nbCont++;
          }
        }
      }
      contour = contour.h_next();
    }
    if (nbCont > 0) {
      save(contourImage, fileName + "-contour");
    }
    return nbCont;
  }

  private void save(IplImage frame, String name) {
    File outputFile = new File(outputFolder, name + ".jpg");
    cvSaveImage(outputFile.getAbsolutePath(), frame);
  }

  private static IplImage copyEmpty(IplImage frame) {
    return IplImage.create(cvGetSize(frame), frame.depth(), frame.nChannels());
  }

  private static BufferedImage toBufferedImage(IplImage src) {
    // IplImage copy = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
    // cvCopy(src, copy);
    return paintConverter.getBufferedImage(grabberConverter.convert(src), 1);
  }

  private static IplImage toIplImage(BufferedImage bufImage) {
    Frame convert = paintConverter.convert(bufImage);
    return new OpenCVFrameConverter.ToIplImage().convert(convert);
  }
}
