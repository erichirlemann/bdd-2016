package org.sonarsource.bdd.java2d;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class Step03BitmapTest {

    @Test
    public void test() throws IOException {
        File srcFile = ImageSamples.IMG_FILE3;
        File dstFile = ImageSamples.resultFile(this, "res.png");

        BufferedImage image = ImageIO.read(srcFile);

        Raster inRaster = image.getRaster();

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster outRaster = outImage.getRaster();

        float[] pollenColor = {0xcb, 0x88, 0x03};
        float[] badColor = {0x15, 0x51, 0xd3};
        float[] color = new float[3];

        long pollenPixels = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                color = inRaster.getPixel(i, j, color);
                if (colorDiff(pollenColor, color) < 0.55) {
                    pollenPixels++;
                    outRaster.setPixel(i, j, color);
                } else {
                    outRaster.setPixel(i, j, badColor);
                }
            }
        }
        System.out.println("pollenPixels = " + pollenPixels);
        ImageIO.write(outImage, "png", dstFile);
    }

    private float colorDiff(float[] colorA, float[] colorB) {
        float lightA = 0;
        float lightB = 0;
        for (int i = 0; i < 3; i++) {
            lightA += colorA[i];
            lightB += colorB[i];
        }
        float lightDiffAB = lightB / lightA;
        float[] colorBShifted = new float[3];
        for (int i = 0; i < 3; i++) {
            colorBShifted[i] = colorB[i] * lightDiffAB;
        }
        float diff = 0;
        for (int i = 0; i < 3; i++) {
            float avg = (colorA[i] + colorBShifted[i]) / 2;
            diff += avg == 0 ? 0 : Math.abs(colorA[i] - colorBShifted[i]) / avg;
        }
        return diff / 3;
    }

}
