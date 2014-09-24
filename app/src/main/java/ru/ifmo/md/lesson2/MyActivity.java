package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.view.View;

public class MyActivity extends Activity {

    ImageView picture;
    Bitmap unchanged,good,fast;
    int[] map;
    int red,blue,green;
    int width = 405;
    int height = 434;
    int initial_width = 700;
    int initial_height = 750;
    double factor = 1.73f;
    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        draw();
    }

    public void draw() {
        map = new int[700 * 750];
        unchanged = BitmapFactory.decodeResource(this.getResources(), R.drawable.source);
        unchanged = Bitmap.createScaledBitmap(unchanged, initial_width, initial_height, true);  //Scaling input img (Can you explain why 700x750 image is decoded as 1400x1500 image?)
        unchanged.getPixels(map, 0, initial_width, 0, 0, initial_width, initial_height);
        picture = (ImageView) findViewById(R.id.picture);
        NearestNeighbour(factor);
        BilinearInterpolation(factor);
        picture.setImageBitmap(fast);
    }

    public void NearestNeighbour(double factor) {
        int[] temp = new int[height * width];
        double px, py;
        for (int i = 0; i < height; i++) {
            for (int j = 1; j < width; j++) {
                px = Math.floor(j * factor);
                py = Math.floor(i * factor);
                temp[height * j - i] = map[(int) ((py * initial_width) + px)];
                temp[height * j - i] = brighter(temp[height * j - i]);
            }
        }
        fast = Bitmap.createBitmap(temp, 0, height, height, width, Bitmap.Config.ARGB_8888);
    }
    public int brighter (int color) {
        blue = ((color & 0xFF) *2);
        if (blue > 255)
            blue = 255;
        green = (((color & 0xFF00) >> 8) *2 );
        if (green > 255)
            green = 255;
        red = (((color & 0xFF0000) >> 16) *2 );
        if (red > 255)
            red = 255;
        return 0xFF000000 + (red << 16) + (green << 8) + blue;
    }

    public void BilinearInterpolation(double factor) {
        int[] temp = new int[height * width];
        int px,py,left,right,top,bottom,red,green,blue,alpha;
        for (int i = 1; i < height - 1; i++)
            for (int j = 1; j < width - 1; j++) {
                px = (int) Math.floor(j * factor);
                py = (int) Math.floor(i * factor);
                top = map[(py - 1) * initial_width + px];
                bottom = map[(py + 1) * initial_width + px];
                left = map[py * initial_width + px - 1];
                right = map[py * initial_width + px + 1];
                blue = ((top & 0xFF) + (bottom & 0xFF) + (left & 0xFF) + (right & 0xFF)) / 4;
                green = (((top & 0xFF00) + (bottom & 0xFF00) + (left & 0xFF00) + (right & 0xFF00)) >> 8) / 4;
                red = (((top & 0xFF0000) + (bottom & 0xFF0000) + (left & 0xFF0000) + (right & 0xFF0000)) >> 16) / 4;
                alpha = 0xFF;
                temp[height * j - i] = (alpha << 24) + (red << 16) + (green << 8) + blue;
            }


        for (int i = 0; i < width * height; i++)
            temp[i] = brighter(temp[i]);
        good = Bitmap.createBitmap(temp, 0, height, height, width, Bitmap.Config.ARGB_8888);
    }

    public void picturechange(View view) {
        if (!clicked) {
            picture.setImageBitmap(good);
        } else {
            picture.setImageBitmap(fast);
        }
        clicked = !clicked;
    }
}