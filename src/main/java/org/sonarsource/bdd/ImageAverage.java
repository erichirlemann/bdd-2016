package org.sonarsource.bdd;


import org.bytedeco.javacpp.opencv_core.IplImage;

import java.io.File;
import java.util.ArrayList;

import static org.bytedeco.javacpp.opencv_core.cvAddWeighted;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

public class ImageAverage {

    public static IplImage averageDichotomy(ArrayList<File> files) {
        if (files.size() < 0 ) {
            throw new IllegalArgumentException("can't compute average image of zero sized set");
        } else if ( files.size() == 2 ) {
            IplImage image1 = cvLoadImage(files.get(0).getAbsolutePath());
            IplImage image2 = cvLoadImage(files.get(1).getAbsolutePath());
            IplImage mean = blankCopy(image1);

            cvAddWeighted(image1, 0.5, image2, 0.5, 0.0, mean);

            cvReleaseImage(image1);
            cvReleaseImage(image2);

            return mean;
        } else if( files.size() == 3 ) {
            IplImage image1 = cvLoadImage(files.get(0).getAbsolutePath());
            IplImage image2 = cvLoadImage(files.get(1).getAbsolutePath());
            IplImage image3 = cvLoadImage(files.get(2).getAbsolutePath());
            IplImage temp = blankCopy(image1);
            IplImage mean = blankCopy(image1);

            cvAddWeighted(image1, 1.0/3.0, image2, 1.0/3.0, 0.0, temp);
            cvAddWeighted(temp, 2.0/3.0, image3, 1.0/3.0, 0.0, mean);

            cvReleaseImage(image1);
            cvReleaseImage(image2);
            cvReleaseImage(image3);
            cvReleaseImage(temp);

            return mean;
        } else {
            int half = files.size()/2;

            ArrayList firstHalf = new ArrayList<>(files.subList(0,half));
            ArrayList secondHalf = new ArrayList<>(files.subList(half+1, files.size() - 1) );

            IplImage firstHalfMean = averageDichotomy(firstHalf);
            IplImage secondHalfMean = averageDichotomy(secondHalf);

            double totalSize = firstHalf.size() + secondHalf.size();
            double firstWeight = firstHalf.size() / totalSize;
            double secondWeight = secondHalf.size() / totalSize;

            IplImage mean = blankCopy(firstHalfMean);
            cvAddWeighted(firstHalfMean, firstWeight, secondHalfMean, secondWeight, 0.0, mean);

            cvReleaseImage(firstHalfMean);
            cvReleaseImage(secondHalfMean);

            return mean;
        }
    }

    private static IplImage blankCopy(IplImage image) {
      return IplImage.create(cvGetSize(image), image.depth(), image.nChannels());
    }
}
