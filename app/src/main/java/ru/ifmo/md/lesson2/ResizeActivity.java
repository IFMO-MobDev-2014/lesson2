package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ResizeActivity extends Activity implements View.OnClickListener {
    static final int SRC_WIDTH = 700;
    static final int SRC_HEIGHT = 750;
    static final int DEST_WIDTH = 405;
    static final int DEST_HEIGHT = 434;
    static final float XY_FACTOR = ((float) SRC_WIDTH / DEST_WIDTH) * ((float) SRC_HEIGHT / DEST_HEIGHT);

    ImageView imageView;
    Bitmap source;
    Bitmap shrunk;
    Bitmap dest;
    boolean quality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        setContentView(imageView);
        source = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.source));
        shrunk = Bitmap.createBitmap(DEST_WIDTH, DEST_HEIGHT, Bitmap.Config.ARGB_8888);
        dest = Bitmap.createBitmap(DEST_HEIGHT, DEST_WIDTH, Bitmap.Config.ARGB_8888);
        qualityShrink();
        postProcess();
        imageView.setOnClickListener(this);
    }

    void qualityShrink() {
        for (int x = 0; x < DEST_WIDTH; ++x) {
            for (int y = 0; y < DEST_HEIGHT; ++y) {
                float fromX = (x + 0.f) * SRC_WIDTH / DEST_WIDTH;
                float fromY = (y + 0.f) * SRC_HEIGHT / DEST_HEIGHT;
                float toX = (x + 1.f) * SRC_WIDTH / DEST_WIDTH;
                float toY = (y + 1.f) * SRC_HEIGHT / DEST_HEIGHT;

                float accR = 0, accG = 0, accB = 0;

                for (int x2 = (int)fromX; x2 < toX; ++x2) {
                    float q1 = x2 < fromX ? 1.f - fromX + x2 : Math.min(toX - x2, 1);
                    for (int y2 = (int)fromY; y2 < toY; ++y2) {
                        float q2 = y2 < fromY ? 1.f - fromY + y2 : Math.min(toY - y2, 1);
                        float q = q1 * q2;

                        int color = source.getPixel(x2, y2);
                        accR += Color.red(color) * q;
                        accG += Color.green(color) * q;
                        accB += Color.blue(color) * q;
                    }
                }

                shrunk.setPixel(x, y, Color.rgb(
                        boundComp(Math.round(accR / XY_FACTOR)),
                        boundComp(Math.round(accG / XY_FACTOR)),
                        boundComp(Math.round(accB / XY_FACTOR))
                ));
            }
        }
        quality = true;
    }

    static int boundComp(int comp) {
        return Math.min(Math.max(comp, 0), 255);
    }

    void fastShrink() {
        for (int x = 0; x < DEST_WIDTH; ++x) {
            for (int y = 0; y < DEST_HEIGHT; ++y) {
                shrunk.setPixel(x, y, source.getPixel(x * SRC_WIDTH / DEST_WIDTH, y * SRC_HEIGHT / DEST_HEIGHT));
            }
        }
        quality = false;
    }

    void postProcess() {
        for (int x = 0; x < DEST_WIDTH; ++x) {
            for (int y = 0; y < DEST_HEIGHT; ++y) {
                int color = shrunk.getPixel(x, y);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                dest.setPixel(DEST_HEIGHT - y - 1, x, Color.rgb(Math.min(r * 7 / 5, 255), Math.min(g * 7 / 5, 255), Math.min(b * 7 / 5, 255)));
            }
        }
        imageView.setImageBitmap(dest);
    }

    @Override
    public void onClick(View view) {
        if (quality) {
            fastShrink();
        } else {
            qualityShrink();
        }
        postProcess();
    }
}
