package org.sonarsource.bdd;

import java.io.File;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BLUR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

public class ImageMotionDetection {

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
    cvThreshold(diff, diff, 130, 255, CV_THRESH_BINARY);
    cvSmooth(diff, diff, CV_BLUR, 9, 9, 2, 2);
    IplImage copy = cvCreateImage(cvGetSize(diff), IPL_DEPTH_8U, 1);
    cvCvtColor(diff, copy, CV_RGB2GRAY);

    cvSaveImage("target/diff.JPG", copy);
    cvReleaseImage(copy);
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

  private static IplImage copy(IplImage frame) {
    //return frame.clone();
    return IplImage.create(cvGetSize(frame), frame.depth(), frame.nChannels());
  }
}