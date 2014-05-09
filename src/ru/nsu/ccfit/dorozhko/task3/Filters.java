package ru.nsu.ccfit.dorozhko.task3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Anton on 25.04.2014.
 */
public class Filters {

    private static double[][] GAUSS_MATRIX = {
            {0.5, 0.75, 0.5},
            {0.75, 1, 0.75},
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

    public static BufferedImage parametrizedGaussBlur(BufferedImage input, double sigma) {

        // build blur kernel
        int size = (int) (((int) (Math.ceil(6 * sigma)) % 2) == 1 ? Math.ceil(6 * sigma) : (Math.ceil(6 * sigma) + 1));
        double kernel[] = new double[size];
        int sigmaIndex = size/2;

        double norm = 0;
        for (int i = -sigmaIndex, j = 0; i <= sigmaIndex; i++, j++) {
            kernel[j] = Math.exp(-Math.pow(i/sigma,2)/2)/(sigma*Math.sqrt(2*Math.PI));
            norm += kernel[j];
        }
        for (int i = -sigmaIndex, j = 0; i <= sigmaIndex; i++, j++) {
            kernel[j] /= norm;
        }


        BufferedImage horizontallyBlurred = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        for (int i = 0; i < horizontallyBlurred.getWidth(); i++)
            for (int j = 0; j < horizontallyBlurred.getHeight(); j++) {

                double newRed = 0;
                double newGreen = 0;
                double newBlue = 0;

                int counter = 0;
                for (int k = -sigmaIndex; k <= sigmaIndex; k++, counter++) {
                    Color c = new Color(input.getRGB(Math.abs(i + k) % horizontallyBlurred.getWidth(), j));
                    newRed += kernel[counter]*c.getRed();
                    newGreen += kernel[counter]*c.getGreen();
                    newBlue += kernel[counter]*c.getBlue();
                }

                horizontallyBlurred.setRGB(i, j, new Color((int) newRed, (int) newGreen, (int) newBlue).getRGB());
            }


        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {

                double newRed = 0;
                double newGreen = 0;
                double newBlue = 0;

                int counter = 0;
                for (int k = -sigmaIndex; k <= sigmaIndex; k++, counter++) {
                    Color c = new Color(horizontallyBlurred.getRGB(i, Math.abs(j + k) % horizontallyBlurred.getHeight()));
                    newRed += kernel[counter]*c.getRed();
                    newGreen += kernel[counter]*c.getGreen();
                    newBlue += kernel[counter]*c.getBlue();
                }

                result.setRGB(i, j, new Color((int) newRed, (int) newGreen, (int) newBlue).getRGB());
            }

        return result;
    }

    public static BufferedImage blackWhite(BufferedImage input, int threshold) {
        double RED_FACTOR = 0.299;
        double BLUE_FACTOR = 0.114;
        double GREEN_FACTOR = 0.587;

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {

                Color c = new Color(input.getRGB(i, j));
                double grey = RED_FACTOR * c.getRed() + GREEN_FACTOR * c.getGreen() + BLUE_FACTOR * c.getBlue();

                if (grey < threshold) {
                    result.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    result.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        return result;
    }

    public static BufferedImage grayScale(BufferedImage input) {
        double RED_FACTOR = 0.299;
        double BLUE_FACTOR = 0.114;
        double GREEN_FACTOR = 0.587;

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {

                Color c = new Color(input.getRGB(i, j));
                double grey = RED_FACTOR * c.getRed() + GREEN_FACTOR * c.getGreen() + BLUE_FACTOR * c.getBlue();

                result.setRGB(i, j, new Color((int) grey, (int) grey, (int) grey).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage negative(BufferedImage input) {

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {

                Color c = new Color(input.getRGB(i, j));

                result.setRGB(i, j,
                        new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
            }
        }
        return result;
    }

    public static BufferedImage fsDithering(BufferedImage input) {

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getHeight(); i++) {
            for (int j = 0; j < result.getWidth(); j++) {

                Color oldColor = new Color(input.getRGB(i, j));

                Color newColor = new Color((oldColor.getRed() / 32) * 32,
                        (oldColor.getGreen() / 32) * 32,
                        (oldColor.getBlue() / 32) * 32);


                result.setRGB(i, j, newColor.getRGB());

                int quantError[] = new int[]{newColor.getRed() - oldColor.getRed(),
                        newColor.getGreen() - oldColor.getGreen(),
                        newColor.getBlue() - oldColor.getBlue()};

                Color changeColor;

                if (i + 1 < result.getHeight()) {
                    changeColor = new Color(input.getRGB(i + 1, j));
                    input.setRGB(i + 1, j,
                            normColor(
                                    changeColor.getRed() + quantError[0] * 7 / 16,
                                    changeColor.getGreen() + quantError[1] * 7 / 16,
                                    changeColor.getBlue() + quantError[2] * 7 / 16
                            ).getRGB()
                    );
                }
                if (j + 1 < input.getWidth() & i + 1 < input.getHeight()) {

                    if (i - 1 > 0) {
                        changeColor = new Color(input.getRGB(i - 1, j + 1));
                        input.setRGB(i + 1, j,
                                normColor(
                                        changeColor.getRed() + quantError[0] * 3 / 16,
                                        changeColor.getGreen() + quantError[1] * 3 / 16,
                                        changeColor.getBlue() + quantError[2] * 3 / 16
                                ).getRGB()
                        );
                    }

                    changeColor = new Color(input.getRGB(i, j + 1));
                    input.setRGB(i + 1, j,
                            normColor(
                                    changeColor.getRed() + quantError[0] * 5 / 16,
                                    changeColor.getGreen() + quantError[1] * 5 / 16,
                                    changeColor.getBlue() + quantError[2] * 5 / 16
                            ).getRGB()
                    );
                    if (i + 1 < input.getWidth()) {
                        changeColor = new Color(input.getRGB(i + 1, j + 1));
                        input.setRGB(i + 1, j,
                                normColor(
                                        changeColor.getRed() + quantError[0] * 1 / 16,
                                        changeColor.getGreen() + quantError[1] * 1 / 16,
                                        changeColor.getBlue() + quantError[2] * 1 / 16
                                ).getRGB()
                        );
                    }
                }
            }
        }

        return result;
    }

    public static BufferedImage orderDithering(BufferedImage input) {
        int[] dither = new int[]{1, 49, 13, 61, 4, 52, 16, 64,
                33, 17, 45, 29, 36, 20, 48, 32,
                9, 57, 5, 53, 12, 60, 8, 56,
                41, 25, 37, 21, 44, 28, 40, 24,
                3, 51, 15, 63, 2, 50, 14, 62,
                35, 19, 47, 31, 34, 18, 46, 30,
                11, 59, 7, 55, 10, 58, 6, 54,
                43, 27, 39, 23, 42, 26, 38, 22};
        int ditheringBase = 64;

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getHeight(); i++) {
            for (int j = 0; j < result.getWidth(); j++) {

                Color oldColor = new Color(input.getRGB(i, j));

                int d = dither[(i & 7) * 8 + (j & 7)];

                Color newColor = normColor(oldColor.getRed() + d,
                        oldColor.getGreen() + d,
                        oldColor.getBlue() + d);

                result.setRGB(i, j,
                        new Color(
                                (newColor.getRed() / ditheringBase) * ditheringBase,
                                (newColor.getGreen() / ditheringBase) * ditheringBase,
                                (newColor.getBlue() / ditheringBase) * ditheringBase)
                                .getRGB()
                );
            }
        }

        return result;
    }

    public static BufferedImage roberts(BufferedImage input) {

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {

                Color tmp1 = new Color(input.getRGB(i, j));
                Color tmp2 = new Color(input.getRGB(Math.abs(i + 1) % result.getWidth(),
                        Math.abs(j + 1) % result.getHeight()));
                Color diff12 = new Color(normalize(tmp1.getRed() - tmp2.getRed()),
                        normalize(tmp1.getGreen() - tmp2.getGreen()),
                        normalize(tmp1.getBlue() - tmp2.getBlue()));

                Color tmp3 = new Color(input.getRGB(Math.abs(i + 1) % result.getWidth(),
                        j));
                Color tmp4 = new Color(input.getRGB(i,
                        Math.abs(j + 1) % result.getHeight()));
                Color diff34 = new Color(normalize(tmp3.getRed() - tmp4.getRed()),
                        normalize(tmp3.getGreen() - tmp4.getGreen()),
                        normalize(tmp3.getBlue() - tmp4.getBlue()));


                int r = (int) Math.sqrt(diff12.getRed() * diff12.getRed() + diff34.getRed() * diff34.getRed());
                int g = (int) Math.sqrt(diff12.getGreen() * diff12.getGreen() + diff34.getGreen() * diff34.getGreen());
                int b = (int) Math.sqrt(diff12.getBlue() * diff12.getBlue() + diff34.getBlue() * diff34.getBlue());

                result.setRGB(i, j, normColor(r, g, b).getRGB());
            }
        }

        return result;
    }

    public static BufferedImage brightness(BufferedImage input, double factor) {

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {

                Color c = new Color(input.getRGB(i, j));

                int r = c.getRed() * factor > 255 ? 255 : (int) (c.getRed() * factor);
                int g = c.getGreen() * factor > 255 ? 255 : (int) (c.getGreen() * factor);
                int b = c.getBlue() * factor > 255 ? 255 : (int) (c.getBlue() * factor);

                result.setRGB(i, j,
                        new Color(r, g, b).getRGB());
            }
        }
        return result;
    }

    static int normalize(double colorComponent) {
        if (colorComponent > 255) {
            return 255;
        }
        if (colorComponent < 0) {
            return 0;
        }
        return (int) colorComponent;
    }

    static Color normColor(double r, double g, double b) {
        return new Color(normalize(r), normalize(g), normalize(b));
    }


    public static BufferedImage sobel(BufferedImage input) {
        int Gx[][] = new int[][]{
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        int Gy[][] = new int[][]{
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());


        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {

                double redX = 0;
                double greenX = 0;
                double blueX = 0;
                double redY = 0;
                double greenY = 0;
                double blueY = 0;


                for (int s = -1; s < 2; s++) {
                    for (int k = -1; k < 2; k++) {
                        Color c = new Color(input.getRGB(Math.abs(i + s) % result.getWidth(),
                                Math.abs(j + k) % result.getHeight()));
                        redX += Gx[s + 1][k + 1] * c.getRed();
                        greenX += Gx[s + 1][k + 1] * c.getGreen();
                        blueX += Gx[s + 1][k + 1] * c.getBlue();

                        redY += Gy[s + 1][k + 1] * c.getRed();
                        greenY += Gy[s + 1][k + 1] * c.getGreen();
                        blueY += Gy[s + 1][k + 1] * c.getBlue();
                    }
                }
                Color diff12 = normColor(redX, greenX, blueX);
                Color diff34 = normColor(redY, greenY, blueY);


                int r = (int) Math.sqrt(diff12.getRed() * diff12.getRed() + diff34.getRed() * diff34.getRed());
                int g = (int) Math.sqrt(diff12.getGreen() * diff12.getGreen() + diff34.getGreen() * diff34.getGreen());
                int b = (int) Math.sqrt(diff12.getBlue() * diff12.getBlue() + diff34.getBlue() * diff34.getBlue());

                result.setRGB(i, j, normColor(r, g, b).getRGB());
            }

        return result;
    }

    public static BufferedImage median(BufferedImage input, int size) {

        BufferedImage result = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());

        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {

                List<Integer> reds = new ArrayList<Integer>();
                List<Integer> greens = new ArrayList<Integer>();
                List<Integer> blues = new ArrayList<Integer>();

                for (int dx = -size / 2; dx < size / 2; dx++) {
                    for (int dy = -size / 2; dy < size / 2; dy++) {
                        Color c = new Color(input.getRGB(Math.abs(i + dx) % result.getWidth(),
                                Math.abs(j + dy) % result.getHeight()));
                        reds.add(c.getRed());
                        greens.add(c.getGreen());
                        blues.add(c.getBlue());
                    }
                }

                Collections.sort(reds);
                Collections.sort(greens);
                Collections.sort(blues);

                result.setRGB(i, j, new Color(
                        reds.get(reds.size() / 2),
                        greens.get(greens.size() / 2),
                        blues.get(blues.size() / 2)).getRGB());
            }

        return result;
    }

    public static BufferedImage sharpen(BufferedImage input) {
        int sharpen[][] = new int[][]{
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };

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
                        newRed += sharpen[s + 1][k + 1] * c.getRed();
                        newGreen += sharpen[s + 1][k + 1] * c.getGreen();
                        newBlue += sharpen[s + 1][k + 1] * c.getBlue();
                    }
                }

                result.setRGB(i, j, normColor(newRed, newGreen, newBlue).getRGB());
            }

        return result;
    }

    public static BufferedImage stamp(BufferedImage input) {
        int stamp[][] = new int[][]{
                {0,  1, 0},
                {-1, 0, 1},
                {0, -1, 0}
        };

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
                        newRed += stamp[s + 1][k + 1] * c.getRed();
                        newGreen += stamp[s + 1][k + 1] * c.getGreen();
                        newBlue += stamp[s + 1][k + 1] * c.getBlue();
                    }
                }

                result.setRGB(i, j, normColor(newRed + 128, newGreen + 128, newBlue + 128).getRGB());
            }

        return result;
    }

    public static BufferedImage magnifyX2(BufferedImage input) {

        BufferedImage result = new BufferedImage(input.getWidth() * 2, input.getHeight() * 2, input.getType());


        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j += 2) {

                if (i % 2 == 0) {
                    result.setRGB(i, j, input.getRGB(i / 2, j / 2));
                } else {
                    Color left = new Color(input.getRGB((i - 1)/2, j / 2));
                    Color right = new Color(input.getRGB((i + 1) % result.getWidth() / 2, j / 2));
                    result.setRGB(i, j, new Color(
                                    (left.getRed() + right.getRed()) / 2,
                                    (left.getGreen() + right.getGreen()) / 2,
                                    (left.getBlue() + right.getBlue()) / 2).getRGB()
                    );
                }
            }
        }

        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 1; j < result.getHeight(); j += 2) {

                Color up = new Color(result.getRGB(i, j - 1));
                Color down = new Color(result.getRGB(i, (j + 1)%result.getHeight()));
                result.setRGB(i, j, new Color(
                                (up.getRed() + down.getRed()) / 2,
                                (up.getGreen() + down.getGreen()) / 2,
                                (up.getBlue() + down.getBlue()) / 2).getRGB()
                );
            }
        }

        return result;
    }
}
