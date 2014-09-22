package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MyActivity extends Activity {

    private Bitmap fastImage = null;
    private Bitmap slowImage = null;
    private static final int WIDTH = 434;
    private static final int HEIGHT = 405;
    private int[] Img = null;
    private int[] rotateImg = null;
    private int cachedW;
    private int cachedH;
    private boolean slow = false;
    ImageView view;
    static int[] table = new int[256];

    private void rotateImage(){
        rotateImg = new int[cachedW * cachedH];
        for (int x = 0; x < cachedH; x++) {
            for (int y = 0; y < cachedW; y++) {
                rotateImg[(y*cachedH) + cachedH - 1 - x] = Img[x * cachedW + y];
            }
        }
        int temp = cachedH;
        cachedH = cachedW;
        cachedW = temp;
    }

    private Bitmap getImage() {
        int w = WIDTH;
        int h = HEIGHT;

        for (int i = 0; i < 256; i++) {
            table[i] = (int) (Math.sqrt(((float) i) / 255.0f) * 255.0f);
        }

        if (Img == null) {
            Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
            cachedH = sourceBitmap.getHeight();
            cachedW = sourceBitmap.getWidth();
            Img = new int[cachedW * cachedH];
            sourceBitmap.getPixels(Img, 0, cachedW, 0, 0, cachedW, cachedH);
        }

        if (rotateImg == null)
            rotateImage();

        float scaleX = 1.73f;
        float scaleY = 1.73f;

        int[] fixedImage = new int[w * h];
        if (!slow) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    fixedImage[y * w + x] = rotateImg[y * cachedW / w * cachedW + x * cachedH / h];
                }
            }
        }
        else {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int x1 = (int)(j * scaleX);
                    int y1 = (int)(i * scaleY);
                    int x2 = (int)((j + 1) * scaleX);
                    int y2 = (int)((i + 1) * scaleY);
                    int k = (x2 - x1 + 1) * (y2 - y1 + 1);
                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    for (int ii = y1; ii <= Math.min(y2, cachedH-1); ii++) {
                        for (int jj = x1; jj <= Math.min(x2, cachedW-1); jj++) {
                                int color = rotateImg[ii * cachedW + jj];
                                red += (color >> 16) & 0xFF;
                                green += (color >> 8) & 0xFF;
                                blue += color & 0xFF;
                            }
                        }
                    fixedImage[i * w + j] = 0xFF000000;
                    fixedImage[i * w + j] |= (red / k) << 16;
                    fixedImage[i * w + j] |= (green / k) << 8;
                    fixedImage[i * w + j] |= (blue / k);
                }
            }
        }

        for (int i = 0; i < fixedImage.length; i++) {
            int r = fixedImage[i] & 0xff;
            int g = (fixedImage[i] & 0xff00) >> 8;
            int b = (fixedImage[i] & 0xff0000) >> 16;
            fixedImage[i] = 0xff000000;
            fixedImage[i] |= (table[b] << 16);
            fixedImage[i] |= (table[g] << 8);
            fixedImage[i] |= r;
        }

        return Bitmap.createBitmap(fixedImage, w, h, Bitmap.Config.ARGB_8888);
    }

    private void processImage()
    {
        if (slow) {
            if (slowImage == null)
                slowImage = getImage();
            view.setImageBitmap(slowImage);
        }
        else {
            if (fastImage == null)
                fastImage = getImage();
            view.setImageBitmap(fastImage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        view = (ImageView) findViewById(R.id.imageView);
        processImage();
    }

    public void onClick(View v) {
        slow = !slow;
        processImage();
    }
}
