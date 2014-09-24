package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MyActivity extends Activity {

    ImageView image;
    Bitmap startBitmap;
    Bitmap newBitmap;
    Bitmap bitmap;
    int width;
    int height;
    int startWidth;
    int startHeight;
    int[] pixels;
    int[] newPixels;
    public static final float coef = 1.73f;
    boolean quality = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mysource);
        image = (ImageView)findViewById(R.id.imageView);
        startWidth = startBitmap.getWidth();
        startHeight = startBitmap.getHeight();
        pixels = new int[startWidth*startHeight];
        newPixels = new int[startWidth*startHeight];
        image.setLongClickable(true);
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                width = startWidth;
                height = startHeight;
                image.setImageResource(android.R.color.transparent);
                bitmap = changeImage(startBitmap);
                image.setImageBitmap(bitmap);
                return true;
            }
        });
    }

    public Bitmap changeImage(Bitmap bitmap) {
        if (quality) {
            bitmap = qualityCompress(bitmap);
        } else bitmap = fastCompress(bitmap);
        bitmap = changeBrightness(bitmap);
        bitmap = doABarrelRoll(bitmap);
        return bitmap;
    }

    public Bitmap changeBrightness(Bitmap bitmap) {
        int red;
        int green;
        int blue;
        newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                red = Color.red(pixels[x+width*y]);
                green = Color.green(pixels[x + width * y]);
                blue = Color.blue(pixels[x + width * y]);
                red = (red<<1 >255) ? 255 : red<<1;
                green = (green<<1 >255) ? 255 : green<<1;
                blue = (blue<<1 >255) ? 255 : blue<<1;
                newBitmap.setPixel(x, y, Color.argb(Color.alpha(pixels[x+width*y]), red, green, blue));
            }
        }
        return newBitmap;
    }

    public Bitmap doABarrelRoll(Bitmap bitmap) {
        newBitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                newBitmap.setPixel(x, y, bitmap.getPixel(y, height-x-1));
            }
        }
        int tmp = width;
        width = height;
        height = tmp;
        return newBitmap;
    }

   public Bitmap fastCompress(Bitmap bitmap) {
        bitmap.getPixels(pixels, 0, width,0 ,0 ,width, height);
        int newHeight = (int) (height/coef);
        int newWidth = (int) (width/coef);
        newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y<newHeight; y++) {
                newPixels[x+ newWidth*y] = pixels[(int)(x * coef) + (int)(y * coef) * width];
            }
        }
        newBitmap.setPixels(newPixels, 0 ,newWidth, 0, 0, newWidth, newHeight);
        width = newWidth;
        height = newHeight;
        return newBitmap;
   }

    public Bitmap qualityCompress(Bitmap bitmap) {
        int prevX;
        int prevY;
        int color01;
        int color00;
        int color10;
        int color11;
        int colorAlpha;
        int colorRed;
        int colorGreen;
        int colorBlue;
        bitmap.getPixels(pixels, 0, width,0 ,0 ,width, height);
        int newHeight = (int) (height/coef);
        int newWidth = (int)(width/coef);
        newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        for (int x = 0; x<newWidth; x++) {
            for (int y = 0; y<newHeight; y++) {
                prevX = (int) (x * coef);
                prevY = (int) (y * coef);
                double dx = (coef * x) - (double) prevX;
                double dy = (coef * y) - (double) prevY;
                color00 = pixels[prevX + width*prevY];
                color01 = pixels[prevX+1+width*prevY];
                color10 = pixels[prevX+width*(prevY+1)];
                color11 = pixels[prevX+1+width*(prevY+1)];
                colorAlpha = (int) ((double) Color.alpha(color00) * (1.0 - dx) * (1.0 - dy) +
                        (double) Color.alpha(color10) * (dx) * (1.0 - dy) +
                        (double) Color.alpha(color01) * (1.0 - dx) * (dy) +
                        (double) Color.alpha(color11) * (dx) * (dy));
                colorRed = (int) ((double) Color.red(color00) * (1.0 - dx) * (1.0 - dy) +
                        (double) Color.red(color10) * (dx) * (1.0 - dy) +
                        (double) Color.red(color01) * (1.0 - dx) * (dy) +
                        (double) Color.red(color11) * (dx) * (dy));
                colorGreen = (int) ((double) Color.green(color00) * (1.0 - dx) * (1.0 - dy) +
                        (double) Color.green(color10) * (dx) * (1.0 - dy) +
                        (double) Color.green(color01) * (1.0 - dx) * (dy) +
                        (double) Color.green(color11) * (dx) * (dy));
                colorBlue = (int) ((double) Color.blue(color00) * (1.0 - dx) * (1.0 - dy) +
                        (double) Color.blue(color10) * (dx) * (1.0 - dy) +
                        (double) Color.blue(color01) * (1.0 - dx) * (dy) +
                        (double) Color.blue(color11) * (dx) * (dy));
                newPixels[x+newWidth*y] =  Color.argb(colorAlpha, colorRed, colorGreen, colorBlue);
            }
        }
        newBitmap.setPixels(newPixels, 0, newWidth, 0, 0 , newWidth, newHeight);
        width = newWidth;
        height = newHeight;
        return newBitmap;
    }

    public void onClick(View view) {
        quality = !quality;
        Toast toastFast = Toast.makeText(getApplicationContext(),
                "Switched to fast compressing mode",
                Toast.LENGTH_SHORT);
        Toast toastQuality = Toast.makeText(getApplicationContext(),
                "Switched to quality compressing mode",
                Toast.LENGTH_SHORT);
        if (quality) {
            toastQuality.show();
        } else toastFast.show();
    }


}
