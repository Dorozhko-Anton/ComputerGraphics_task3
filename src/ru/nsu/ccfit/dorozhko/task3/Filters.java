package ru.nsu.ccfit.dorozhko.task3;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Anton on 25.04.2014.
 */
public class Filters {

    private static double[][] GAUSS_MATRIX = {
            {0.5 , 0.75 , 0.5} ,
            {0.75 , 1 , 0.75} ,
            {0.5, 0.75, 0.5}
    };

    public static BufferedImage gaussBlur(BufferedImage input) {
        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {

                double newRed = 0;
                double newGreen = 0;
                double newBlue = 0;

                for (int s = -1; s < 2; s++) {
                    for (int k = -1; k < 2; k++) {
                        Color c = new Color(input.getRGB(Math.abs(i + s) % result.getWidth(),
                                Math.abs(j + k) % result.getHeight()));
                        newRed += GAUSS_MATRIX[s + 1][k + 1] * c.getRed();
                        newGreen += GAUSS_MATRIX[s + 1][k + 1] * c.getGreen();
                        newBlue += GAUSS_MATRIX[s + 1][k + 1] * c.getBlue();
                    }
                }
                newRed /= 6;
                newGreen /= 6;
                newBlue /= 6;

                result.setRGB(i, j, new Color((int) newRed, (int) newGreen, (int) newBlue).getRGB());
            }

        return result;
    }
}
