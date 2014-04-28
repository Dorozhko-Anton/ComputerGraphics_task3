package ru.nsu.ccfit.dorozhko.task3;

import com.sun.java.util.jar.pack.*;

import com.sun.media.sound.InvalidFormatException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by Anton on 25.04.2014.
 */
public class BMPcontainer {
    // file header
        char id[] = new char[2];
        int fsize;
        short reservedByApp1;
        short reservedByApp2;
        int offset;

    // header info
        int headerSize;
        int width;
        int height;
        short colorPlane;
        short bpp; // bits per pixel
        int compression;
        int pixelArraySize;
        int ppmWidth; // pixel per meter
        int ppmHeight;
        int palette;
        int importantColors;

     Color[][] pixelArray;

    public BMPcontainer(File file) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));


        id[0] = (char) dataInputStream.readByte();
        id[1] = (char) dataInputStream.readByte();

        if ('B' != id[0] && 'M' != id[1]) {
            throw new InvalidFormatException("not BMP file");
        }

        fsize = Integer.reverseBytes(dataInputStream.readInt());
        reservedByApp1 = Short.reverseBytes(dataInputStream.readShort());
        reservedByApp2 = Short.reverseBytes(dataInputStream.readShort());
        offset = Integer.reverseBytes(dataInputStream.readInt());
        // header info
        headerSize = Integer.reverseBytes(dataInputStream.readInt());
        width = Integer.reverseBytes(dataInputStream.readInt());
        height = Integer.reverseBytes(dataInputStream.readInt());
        colorPlane = Short.reverseBytes(dataInputStream.readShort());
        bpp = Short.reverseBytes(dataInputStream.readShort());
        compression = Integer.reverseBytes(dataInputStream.readInt());
        pixelArraySize = Integer.reverseBytes(dataInputStream.readInt());
        ppmWidth = Integer.reverseBytes( dataInputStream.readInt());
        ppmHeight = Integer.reverseBytes(dataInputStream.readInt());
        palette = Integer.reverseBytes(dataInputStream.readInt());
        importantColors = Integer.reverseBytes(dataInputStream.readInt());



        pixelArray = new Color[width][height];

        int i = 0;
        int j = 0;

            for (i = height - 1; i >= 0; i--) {
                for (j = 0; j < width; j++) {
                    int b = dataInputStream.read();
                    int g = dataInputStream.read();
                    int r = dataInputStream.read();

                    pixelArray[j][i] = new Color(r, g, b);

                }
            }

        dataInputStream.close();
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color get(int i, int j) {
        return pixelArray[i][j];
    }



    /**
     * paints image
     *
     * @param g - bufferedImage
     */
    public void paint(BufferedImage g) {
            for (int i = height - 1; i >= 0; i--) {
                for (int j = 0; j < width; j++) {
                    g.setRGB(j, i, pixelArray[j][i].getRGB());
                }
            }
    }


    public static void save(BufferedImage image, File file) throws IOException {
        DataOutputStream dataInputStream = new DataOutputStream(new FileOutputStream(file));


        dataInputStream.writeByte('B');
        dataInputStream.writeByte('M');
        dataInputStream.writeInt(Integer.reverseBytes(image.getHeight() * image.getWidth() * 4));
        dataInputStream.writeShort(Short.reverseBytes((short) 0));
        dataInputStream.writeShort(Short.reverseBytes((short) 0));
        dataInputStream.writeInt(Integer.reverseBytes(54));

        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(image.getWidth()));
        dataInputStream.writeInt(Integer.reverseBytes(image.getHeight()));
        dataInputStream.writeShort(Short.reverseBytes((short) 0));
        dataInputStream.writeShort(Short.reverseBytes((short) 0));
        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(0));
        dataInputStream.writeInt(Integer.reverseBytes(0));



        int i = 0;
        int j = 0;

        for (i = image.getHeight() - 1; i >= 0; i--) {
            for (j = 0; j < image.getWidth(); j++) {

                Color c = new Color( image.getRGB(j, i));

                dataInputStream.writeByte( c.getBlue());
                dataInputStream.writeByte( c.getGreen());
                dataInputStream.writeByte( c.getRed());

            }
        }
        dataInputStream.close();
    }

    public BufferedImage getSubImage(int x, int y, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result.setRGB(i , j, pixelArray[x + i][y + j].getRGB());
            }
        }
        return result;
    }

    public double getAspectRatio() {
        return width*1./height;
    }

    public static BufferedImage compress(BMPcontainer bmPcontainer) {
        int newWidth;
        int newHeight;

        double ar = bmPcontainer.getAspectRatio();
        if (ar > 1) {
            newWidth = 256;
            newHeight = (int) (newWidth / ar);
        } else {
            newHeight = 256;
            newWidth = (int) (newHeight * ar);
        }

        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double widthStep = bmPcontainer.getWidth() / newWidth;
        double heightStep = bmPcontainer.getHeight() / newHeight;
        int sourceX = 0;
        int sourceY = 0;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                result.setRGB(x, y, bmPcontainer.get(sourceX, sourceY).getRGB());
                sourceX += widthStep;
            }
            sourceY += heightStep;
            sourceX = 0;
        }

        return result;
    }
}
