package ru.ifmo.md.lesson2;

/**
 * Created by Anton Borzenko on 10.11.2014.
 * Class processing images with ArrayImage type
 */

public class ImageEditor {
    public static enum ScaleMode { SLOW, FAST }
    private static void toARGB(int[] argb, int color) {
        for (int i = 3; i >= 0; i--) {
            argb[i] = color & 0xFF;
            color >>=  8;
        }
    }
    private static int fromARGB(int[] argb) {
        return (argb[0] << 24) |
               (argb[1] << 16) |
               (argb[2] <<  8) |
                argb[3];
    }
    public static ArrayImage scale(ArrayImage image, float scale, ScaleMode scaleMode) {
        if (image == null) {
            return null;
        }
        int width  = image.getWidth();
        int height = image.getHeight();
        int newWidth  = (int)(width  * scale);
        int newHeight = (int)(height * scale);
        int[] colors1 = image.getImage();
        int[] colors2 = new int[newWidth * newHeight];
        int[] sum = new int[4], argb = new int[4];
        if (scaleMode == ScaleMode.SLOW && scale < 1.0f) {
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    int x1 = (int)(x / scale);
                    int y1 = (int)(y / scale);
                    int x2 = (int)((x + 1) / scale);
                    int y2 = (int)((y + 1) / scale);
                    int cnt = 0;
                    for (int i = 0; i < 4; i++) {
                        sum[i] = 0;
                    }
                    for (int i = y1; i < y2; i++) {
                        for (int j = x1; j < x2; j++) {
                            toARGB(argb, colors1[j + i * width]);
                            for (int k = 0; k < 4; k++) {
                                sum[k] += argb[k];
                            }
                            cnt++;
                        }
                    }
                    if (cnt != 0) {
                        for (int i = 0; i < 4; i++) {
                            sum[i] /= cnt;
                        }
                        colors2[x + y * newWidth] = fromARGB(sum);
                    } else {
                        colors2[x + y * newWidth] = colors1[(int) (x / scale) + (int) (y / scale) * width];
                    }
                }
            }
        } else {
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    colors2[x + y * newWidth] = colors1[(int) (x / scale) + (int) (y / scale) * width];
                }
            }
        }
        return new ArrayImage(colors2, newWidth, newHeight);
    }
    private static int transform(int times, int x, int y, int width, int height) {
        switch (times) {
            case 0:
                return x + y * width;
            case 1:
                return y + (width - x - 1) * height;
            case 2:
                return (width - x - 1) + (height - y - 1) * width;
            default:
                return (height - y - 1) + x * height;
        }
    }
    public static ArrayImage rotate(ArrayImage image, int times) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth(), height = image.getHeight();

        times %= 4;
        if (times < 0) {
            times += 4;
        }

        int[] colors1 = image.getImage(), colors2 = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                colors2[transform(times, x, y, width, height)] = colors1[x + y * width];
            }
        }
        if (times == 1 || times == 3) {
            int temp = height;
            height = width;
            width  = temp;
        }
        return new ArrayImage(colors2, width, height);
    }
    public static ArrayImage changeBrightness(ArrayImage image, float ratio) {
        if (image == null) {
            return null;
        }

        int[] colors = image.getImage().clone();
        int[] argb = new int[4];
        for (int i = colors.length - 1; i >= 0; i--) {
            toARGB(argb, colors[i]);
            for (int j = 1; j < 4; j++) {
                argb[j] *= ratio;
                if (argb[j] > 0xFF) argb[j] = 0xFF;
            }
            colors[i] = fromARGB(argb);
        }
        return new ArrayImage(colors, image.getWidth(), image.getHeight());
    }
}