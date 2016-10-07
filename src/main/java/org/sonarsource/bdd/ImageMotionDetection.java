package org.sonarsource.bdd;

import java.io.File;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;

import static org.bytedeco.javacpp.helper.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvAnd;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
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

public class ImageMotionDetection {

  // static opencv_core.CvScalar min = cvScalar(181, 137, 28, 0);// BGR-A
  // static opencv_core.CvScalar max = cvScalar(228, 175, 59, 0);// BGR-A

  static opencv_core.CvScalar min = cvScalar(0x09, 0x63, 0x90, 0);// BGR-A
  static opencv_core.CvScalar max = cvScalar(0x2c, 0xB3, 0xe4, 0);// BGR-A

  public void execute() throws FrameGrabber.Exception {
    File filePath = new File(getClass().getClassLoader().getResource("samples").getFile());
    File file1 = new File(filePath, "DSC_0218-min.JPG");
    File file2 = new File(filePath, "DSC_0219-min.JPG");
    File file3 = new File(filePath, "DSC_0220-min.JPG");
    if (!file1.exists()) {
      throw new IllegalArgumentException("File not found");
    }

    IplImage image1 = cvLoadImage(file1.getAbsolutePath());
    IplImage image2 = cvLoadImage(file2.getAbsolutePath());

    IplImage diff = diff(image1, image2);
    // do some threshold for wipe away useless details
    cvThreshold(diff, diff, 100, 255, CV_THRESH_BINARY);
    cvSmooth(diff, diff, CV_BLUR, 9, 9, 2, 2);
    save(diff, "diff");

    IplImage mask = cvCreateImage(cvGetSize(diff), IPL_DEPTH_8U, 1);
    cvCvtColor(diff, mask, CV_RGB2GRAY);
    // IplImage newMask = copy(image1);
    // cvCvtColor(mask, newMask, CV_GRAY2RGB);
    save(mask, "mask");

    IplImage copyAnd = copy(image2);
    cvAnd(image2, image2, copyAnd, mask);
    save(copyAnd, "and");

    IplImage result = cvCreateImage(cvGetSize(image1), IPL_DEPTH_8U, 1);
    cvInRangeS(copyAnd, min, max, result);

    contours(result);

    save(result, "final");
    cvReleaseImage(copyAnd);
    cvReleaseImage(image1);
    cvReleaseImage(image2);
  }

  private IplImage diff(IplImage frame, IplImage frame2) {
    IplImage diff = copy(frame);

    // cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
    // perform ABS difference
    cvAbsDiff(frame, frame2, diff);
    return diff;
  }

  private void contours(IplImage image) {
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
          opencv_core.CvPoint2D32f center = box.center();
          opencv_core.CvSize2D32f size = box.size();
          System.out.println("sizes : " + size);
          if (size.width() * size.height() > 1000) {
            nbCont++;
          }
        }
      }
      contour = contour.h_next();
    }
    System.out.println("Contours : " + nbCont);
    save(contourImage, "contour");
  }

  private static void save(IplImage frame, String name) {
    cvSaveImage(String.format("target/%s.JPG", name), frame);
  }

  private static IplImage copy(IplImage frame) {
    // return frame.clone();
    return IplImage.create(cvGetSize(frame), frame.depth(), frame.nChannels());
  }
}
