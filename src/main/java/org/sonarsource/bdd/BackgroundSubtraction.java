package org.sonarsource.bdd;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import static org.bytedeco.javacpp.opencv_core.CvBox2D;
import static org.bytedeco.javacpp.opencv_core.CvContour;
import static org.bytedeco.javacpp.opencv_core.CvMemStorage;
import static org.bytedeco.javacpp.opencv_core.CvPoint2D32f;
import static org.bytedeco.javacpp.opencv_core.CvSeq;
import static org.bytedeco.javacpp.opencv_core.CvSize2D32f;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.IplImage;
import static org.bytedeco.javacpp.opencv_core.cvAbsDiff;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvFindContours;
import static org.bytedeco.javacpp.opencv_imgproc.cvMinAreaRect2;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;

public class BackgroundSubtraction {

  public void execute2() {
    String photo = "DSC_0046.JPG";
    String dir = "/Users/julienlancelot/Downloads/BDD/";
    String temp = "/Users/julienlancelot/Downloads/Temp/";
    opencv_core.IplImage iplImage = cvLoadImage(dir + photo);
    if (iplImage == null) {
      throw new IllegalArgumentException("Unknown image");
    }
    cvSmooth(iplImage, iplImage, CV_GAUSSIAN, 15, 15, 0d, 0d);
    cvSaveImage(temp + photo, iplImage);
    cvReleaseImage(iplImage);
  }

  public void imageMotionDetector(){

  }

  public void videoMotionDetector() throws FrameGrabber.Exception {
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
    grabber.start();

    IplImage frame = converter.convert(grabber.grab());
    IplImage image = null;
    IplImage prevImage = null;
    IplImage diff = null;

    CanvasFrame canvasFrame = new CanvasFrame("Some Title");
    canvasFrame.setCanvasSize(frame.width(), frame.height());

    CvMemStorage storage = CvMemStorage.create();

    while (canvasFrame.isVisible() && (frame = converter.convert(grabber.grab())) != null) {
      cvClearMemStorage(storage);

      cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
      if (image == null) {
        image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(frame, image, CV_RGB2GRAY);
      } else {
        prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
        prevImage = image;
        image = opencv_core.IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(frame, image, CV_RGB2GRAY);
      }

      if (diff == null) {
        diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
      }

      if (prevImage != null) {
        // perform ABS difference
        cvAbsDiff(image, prevImage, diff);
        // do some threshold for wipe away useless details
        cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);

        canvasFrame.showImage(converter.convert(diff));

        // recognize contours
        CvSeq contour = new CvSeq(null);
        cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

        while (contour != null && !contour.isNull()) {
          if (contour.elem_size() > 0) {
            CvBox2D box = cvMinAreaRect2(contour, storage);
            // test intersection
            if (box != null) {
              CvPoint2D32f center = box.center();
              CvSize2D32f size = box.size();
              /*
               * for (int i = 0; i < sa.length; i++) {
               * if ((Math.abs(center.x - (sa[i].offsetX + sa[i].width / 2))) < ((size.width / 2) + (sa[i].width / 2)) &&
               * (Math.abs(center.y - (sa[i].offsetY + sa[i].height / 2))) < ((size.height / 2) + (sa[i].height / 2))) {
               * if (!alarmedZones.containsKey(i)) {
               * alarmedZones.put(i, true);
               * activeAlarms.put(i, 1);
               * } else {
               * activeAlarms.remove(i);
               * activeAlarms.put(i, 1);
               * }
               * System.out.println("Motion Detected in the area no: " + i +
               * " Located at points: (" + sa[i].x + ", " + sa[i].y+ ") -"
               * + " (" + (sa[i].x +sa[i].width) + ", "
               * + (sa[i].y+sa[i].height) + ")");
               * }
               * }
               */
            }
          }
          contour = contour.h_next();
        }
      }
    }
    grabber.stop();
    canvasFrame.dispose();
  }

}
