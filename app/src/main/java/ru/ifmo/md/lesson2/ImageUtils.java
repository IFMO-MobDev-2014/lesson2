package ru.ifmo.md.lesson2;

import android.graphics.Color;

/**
 * Created by vlad on 12.11.14.
 */

public class ImageUtils {

    public static int[] fastResize(int[] image, int width, int height, int dstWidth, int dstHeight) {
        int[] result = new int[dstWidth * dstHeight];

        double scaleX = (double) width / dstWidth;
        double scaleY = (double) height / dstHeight;

        for(int y = 0; y < dstHeight; y++) {
            for (int x = 0; x < dstWidth; x++) {
                result[y * dstWidth + x] = image[(int) (y * scaleY) * width + (int) (x * scaleX)];
            }
        }

        return result;
    }

    private static double cubicInterpolation(double[] arr, double x) {
       return (-0.5 * arr[0] + 1.5 * arr[1] - 1.5 * arr[2] + 0.5 * arr[3]) * x * x * x + (arr[0] - 2.5 * arr[1] + 2.0 * arr[2] - 0.5 * arr[3]) * x * x  + (-0.5 * arr[0] + 0.5 * arr[2]) * x + arr[1];
    }

    private static double bicubicInterpolation(double[][] arr, double x, double y) {
        double[] tmp = new double[4];
        
        for (int i = 0; i < 4; i++) {
            tmp[i] = cubicInterpolation(arr[i], y);
        }

        return cubicInterpolation(tmp, x);
    }

    public static int[] qualityResize(int[] image, int width, int height, int dstWidth, int dstHeight) {
        int[] result = new int[dstWidth * dstHeight];

        double scaleX = (double) width / dstWidth;
        double scaleY = (double) height / dstHeight;

        for(int y = 0; y < dstHeight; y++) {
            for (int x = 0; x < dstWidth; x++) {

                double xPos = x * scaleX;
                double yPos = y * scaleY;

                int xPosI = (int) xPos;
                int yPosI = (int) yPos;

                if (xPosI + 3 >= width || yPosI + 3 >= height) {
                    result[y * dstWidth + x] = image[(int) (y * scaleY) * width + (int) (x * scaleX)];
                    continue;
                }

                double[][] arrA = new double[4][4];
                double[][] arrR = new double[4][4];
                double[][] arrG = new double[4][4];
                double[][] arrB = new double[4][4];
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        int color = image[(j + yPosI) * width + i + xPosI];
                        arrA[i][j] = Color.alpha(color);
                        arrR[i][j] = Color.red  (color);
                        arrG[i][j] = Color.green(color);
                        arrB[i][j] = Color.blue (color);
                    }
                }

                int alpha = (int) bicubicInterpolation(arrA, xPos - xPosI, yPos - yPosI);
                int red   = (int) bicubicInterpolation(arrR, xPos - xPosI, yPos - yPosI);
                int green = (int) bicubicInterpolation(arrG, xPos - xPosI, yPos - yPosI);
                int blue  = (int) bicubicInterpolation(arrB, xPos - xPosI, yPos - yPosI);

                alpha = Math.min(alpha, 255);
                red   = Math.min(red  , 255);
                green = Math.min(green, 255);
                blue  = Math.min(blue , 255);

                alpha = Math.max(alpha, 0);
                red   = Math.max(red,   0);
                green = Math.max(green, 0);
                blue  = Math.max(blue,  0);

                result[y * dstWidth + x] = Color.argb(alpha, red, green, blue);
            }
        }

        return result;
    }

    public static int[] rotate(int[] image, int width, int height) {
        int[] res = new int[width * height];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                res[y * height + x] = image[(height - 1 - x) * width + y];
            }
        }

        return res;
    }

    public static int[] doubleBrightness(int[] image) {

        for (int i = 0; i < image.length; i++) {

            int alpha = Color.alpha(image[i]);
            int red   = Color.red  (image[i]);
            int green = Color.green(image[i]);
            int blue  = Color.blue (image[i]);
            
            alpha = Math.min(alpha * 2, 255);
            red   = Math.min(red   * 2, 255);
            green = Math.min(green * 2, 255);
            blue  = Math.min(blue  * 2, 255);

            image[i] = Color.argb(alpha, red, green, blue);

        }

        return image;
    }

}