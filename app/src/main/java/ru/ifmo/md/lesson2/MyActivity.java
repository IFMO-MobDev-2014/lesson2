package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

/**
 * Created by SergeyBudkov on 19.09.2014.
 */

public class MyActivity extends Activity {

    static Bitmap newFastPicture;
    static Bitmap newQualityPicture;
    static Bitmap sourcePicture;
    int sourcePixels[][] = new int[700][750];
    int rotatedPixels[][] = new int[700][750];
    int pictureNewPixels[][] = new int[405][434];
    int alphaGrad;
    int redGrad;
    int greenGrad;
    int blueGrad;
    int div = 0;
    int x, y;
    private PictureView PictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        sourcePicture = BitmapFactory.decodeResource(getResources(), R.drawable.source, options);
        for (int i = 0; i < 700; i++)
            for (int j = 0; j < 750; j++) {
                sourcePixels[i][j] = sourcePicture.getPixel(i, j);
            }

        rotatePicture();
        brighterPicture();
        fastNewPicture();
        qualityNewPicture();
        PictureView = new PictureView(this);
        setContentView(PictureView);
    }

    public void rotatePicture() {
        for (int i = 700 - 1; i >= 0; i--)
            for (int j = 0; j < 750; j++) {
                rotatedPixels[i][750 - 1 - j] = sourcePixels[i][j];
            }
    }

    public void brighterPicture() {
        for (int i = 0; i < 700; i++)
            for (int j = 0; j < 750; j++)
                rotatedPixels[i][j] = Color.argb(Math.min(255, (int) (Color.alpha(rotatedPixels[i][j]) * 1.5)), Math.min(255, (int) (Color.red(rotatedPixels[i][j]) * 1.5)),
                        Math.min(255, (int) (Color.green(rotatedPixels[i][j]) * 1.5)), Math.min(255, (int) (Color.blue(rotatedPixels[i][j]) * 1.5)));
    }

    public void createNewPixel(int pixX, int pixY, int lX, int rX, int uY, int dY) {
        pictureNewPixels[pixX][pixY] = Color.argb((Color.alpha(rotatedPixels[lX][uY]) + Color.alpha(rotatedPixels[rX][uY]) + Color.alpha(rotatedPixels[lX][dY]) + Color.alpha(rotatedPixels[rX][dY])) / 4,
                (Color.red(rotatedPixels[lX][uY]) + Color.red(rotatedPixels[rX][uY]) + Color.red(rotatedPixels[lX][dY]) + Color.red(rotatedPixels[rX][dY])) / 4,
                (Color.green(rotatedPixels[lX][uY]) + Color.green(rotatedPixels[rX][uY]) + Color.green(rotatedPixels[lX][dY]) + Color.green(rotatedPixels[rX][dY])) / 4,
                (Color.blue(rotatedPixels[lX][uY]) + Color.blue(rotatedPixels[rX][uY]) + Color.blue(rotatedPixels[lX][dY]) + Color.blue(rotatedPixels[rX][dY])) / 4);
    }

    public void fastNewPicture() {
        int left_x;
        int up_y;
        int right_x;
        int down_y;
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++) {
                up_y = (int) (1.73 * j);
                down_y = up_y;
                if (up_y + 1 < 750)
                    down_y++;
                left_x = (int) (1.73 * i);
                right_x = left_x;
                if (left_x + 1 < 700)
                    right_x++;
                createNewPixel(i, j, left_x, right_x, up_y, down_y);
            }
        int newPixels[] = new int[405 * 434];
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++)
                newPixels[j + i * 405] = pictureNewPixels[i][j];
        newFastPicture = Bitmap.createBitmap(newPixels, 0, 405, 405, 434, Bitmap.Config.ARGB_8888);
    }

    public void nextNewPixel(int i, int j) {
        for (x = Math.max(0, (int) (i * 1.73) - 2); x < Math.min(700, (int) (i * 1.73) + 2); x++)
            for (y = Math.max(0, (int) (j * 1.73) - 2); y < Math.min(750, (int) (j * 1.73) + 2); y++) {
                alphaGrad += Color.alpha(rotatedPixels[x][y]);
                redGrad += Color.red(rotatedPixels[x][y]);
                greenGrad += Color.green(rotatedPixels[x][y]);
                blueGrad += Color.blue(rotatedPixels[x][y]);
                div++;
            }
    }

    public void qualityNewPicture() {
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++) {
                alphaGrad = 0;
                redGrad = 0;
                greenGrad = 0;
                blueGrad = 0;
                div = 0;
                nextNewPixel(i, j);
                pictureNewPixels[i][j] = Color.argb(alphaGrad / div, redGrad / div, greenGrad / div, blueGrad / div);
            }
        int newPixels[] = new int[405 * 434];
        for (int i = 0; i < 405; i++)
            for (int j = 0; j < 434; j++)
                newPixels[j + i * 405] = pictureNewPixels[i][j];
        newQualityPicture = Bitmap.createBitmap(newPixels, 0, 405, 405, 434, Bitmap.Config.ARGB_8888);
    }

}
