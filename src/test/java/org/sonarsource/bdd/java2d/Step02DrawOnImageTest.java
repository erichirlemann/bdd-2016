package org.sonarsource.bdd.java2d;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Test;

public class Step02DrawOnImageTest {

    @Test
    public void test() throws IOException {
        File srcFile = ImageSamples.IMG_FILE3;
        File dstFile = ImageSamples.resultFile(this, "res.jpg");

        BufferedImage image = ImageIO.read(srcFile);

        Graphics2D g2 = (Graphics2D) image.getGraphics();

        g2.setColor(Color.blue);

        float dash[] = {20.0f};
        BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 20.0f, dash, 0.0f);
        g2.setStroke(dashed);

        double x = 990;
        double y = 1188;
        double r = 30;
        g2.draw(new Ellipse2D.Double(x-r, y-r, r*2, r*2));

        ImageIO.write(image, "jpg", dstFile);
    }

}
