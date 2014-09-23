package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity {
    private static final int Old_Width = 750;
    private static final int Old_Height = 700;
    private static final int New_Width = 434;
    private static final int New_Height = 405;
    private ImageView imageView;
    int [] pixels = new int [Old_Height * Old_Width];
    Bitmap bitmap;
    Bitmap FastBitmap = Bitmap.createBitmap(New_Width, New_Height, Bitmap.Config.ARGB_8888);
    Bitmap QualityBitmap = Bitmap.createBitmap(New_Width, New_Height, Bitmap.Config.ARGB_8888);
    boolean flag = true;

    private Bitmap RotateCW(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int [] new_pixels = new int [width * height];
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                new_pixels[y + x * height] = pixels[x + (height - y - 1) * width];
            }
        }
        return Bitmap.createBitmap(new_pixels, height, width, Bitmap.Config.ARGB_8888);
    }

    private Bitmap Brightness(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {  /*y + x * height*/
                int colorRed = (0xff + Color.red(pixels[x + y * width])) / 2;
                int colorGreen = (0xff + Color.green(pixels[x + y * width])) / 2;
                int colorBlue = (0xff + Color.blue(pixels[x + y * width])) / 2;
                pixels[x + y * width] = Color.rgb(colorRed, colorGreen, colorBlue);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private Bitmap FastScale(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int [] new_pixels = new int [New_Width * New_Height];
        for (int x = 0; x < New_Width; x++) {
            for (int y = 0; y < New_Height; y++) {
                new_pixels[x + y * New_Width] = pixels[(int)(x * 1.73f) +
                        width * ((int)(y * 1.73f))];
            }
        }
        FastBitmap.setPixels(new_pixels, 0, New_Width, 0, 0, New_Width, New_Height);
        return FastBitmap;
    }

    private Bitmap QualityScale(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] new_pixels = new int[New_Width * New_Height];
        for (int y = 0; y < New_Height; y++) {float tmp = (float) y / (New_Height - 1) * (height - 1);
            int h = (int) Math.floor(tmp);
            if (h < 0) {
                h = 0;
            } else {
                if (h > height - 2) {
                    h = height - 2;
                }
            }
            float u = (tmp - h);
            for (int x = 0; x < New_Width; x++) {
                tmp = (float) x / (New_Width - 1) * (width - 1);
                int w = (int) Math.floor(tmp);
                if (w < 0) {
                    w = 0;
                } else {
                    if (w > width - 2) {
                        w = width - 2;
                    }
                }
                float t = tmp - w;
                float d1 = (1 - t) * (1 - u);
                float d2 = t * (1 - u);
                float d3 = t * u;
                float d4 = (1 - t) * u;

                int p1 = pixels[h * width + w];
                int p2 = pixels[h * width + w + 1];
                int p3 = pixels[(h + 1) * width + w + 1];
                int p4 = pixels[(h + 1) * width + w + 1];
                int alpha = (int) (Color.alpha(p1) * d1 + Color.alpha(p2) * d2 + Color.alpha(p3) * d3 + Color.alpha(p4) * d4);
                int red = (int) (Color.red(p1) * d1 + Color.red(p2) * d2 + Color.red(p3) * d3 + Color.red(p4) * d4);
                int green = (int) (Color.green(p1) * d1 + Color.green(p2) * d2 + Color.green(p3) * d3 + Color.green(p4) * d4);
                int blue = (int) (Color.blue(p1) * d1 + Color.blue(p2) * d2 + Color.blue(p3) * d3 + Color.blue(p4) * d4);

                new_pixels[y * New_Width + x] = Color.argb(alpha, red, green, blue);
            }
        }
        QualityBitmap.setPixels(new_pixels, 0, New_Width, 0, 0, New_Width, New_Height);
        return QualityBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        bitmap = RotateCW(BitmapFactory.decodeResource(getResources(), R.drawable.source)).copy(Bitmap.Config.ARGB_8888, true);
        bitmap = (Brightness(bitmap));
        FastBitmap = FastScale(bitmap);
        QualityBitmap = QualityScale(bitmap);
        imageView.setImageBitmap(QualityBitmap);
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!flag)
                    imageView.setImageBitmap(QualityBitmap);
                else
                    imageView.setImageBitmap(FastBitmap);
                flag = !flag;
            }
        });
    }
}