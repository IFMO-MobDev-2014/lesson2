package com.example.alexey.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView textView;
    ImageView iv1,
            iv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView) findViewById(R.id.textView);
        iv1 = (ImageView) findViewById(R.id.imageView);
        iv2 = (ImageView) findViewById(R.id.imageView2);
        getPixels();
        createImages();
        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.INVISIBLE);
        iv1.setMinimumWidth(nWidth);
        iv1.setMinimumHeight(nHeight);
        iv1.setImageBitmap(fancy);
        textView.setText("Fancy");
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.VISIBLE);
                textView.setText("Fast");
            }
        });
        iv2.setMinimumWidth(nWidth);
        iv2.setMinimumHeight(nHeight);
        iv2.setImageBitmap(fast);
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv2.setVisibility(View.INVISIBLE);
                iv1.setVisibility(View.VISIBLE);
                textView.setText("Fancy");
            }
        });
    }


    static int width = 700,
            height = 750,
            nWidth = 405,
            nHeight = 434;
    Bitmap fast = Bitmap.createBitmap(nHeight, nWidth, Bitmap.Config.ARGB_8888),
            fancy = Bitmap.createBitmap(nHeight, nWidth, Bitmap.Config.ARGB_8888),
            colors,
            bitmap = null;
    int color,
            colorA,
            colorR,
            colorG,
            colorB;

    private void getPixels() {
        colors = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.source);
        int k = bitmap.getHeight() / height;
        int x=0,
            y=0;
        for (int i = 0; i < height; i++) {
            x=0;
            for (int j = 0; j < width; j++) {
                color = bitmap.getPixel((int) x, (int) y);
                colorA = (color >> 24) & 0xff;
                colorR = (color >> 16) & 0xff;
                colorG = (color >> 8) & 0xff;
                colorB = color & 0xff;
                colors.setPixel(749 - i, j, (colorA << 24) + (increesBrightness(colorR) << 16)
                        + (increesBrightness(colorG) << 8)
                        + increesBrightness(colorB));
                x+=k;
            }
            y+=k;
        }
    }

    private static int increesBrightness(int c) {
        if (c * 2 > 0xff) return 0xff;
        return c * 2;
    }

    public void createImages() {
        float distanceX = (float) width / nWidth;
        float distanceY = (float) height / nHeight;
        float x = 0;
        float y;
        for (int i = 0; i < nWidth; i++) {
            y = height;
            for (int j = 0; j < nHeight; j++) {
                y -= distanceY;
                fast.setPixel(j, i, colors.getPixel(749 - (int) y, (int) x));
                fancy.setPixel(j, i, biPolInt(749 - (int) y, (int) x));
            }
            x += distanceX;
        }

    }

    int p0,
            p1,
            p2,
            p3,
            a,
            r,
            g,
            b;

    private int biPolInt(float x, float y) {
        if (x > 0 && y > 0 && x < width - 1 && y < height - 1) {
            p0 = colors.getPixel((int) (x - 1), (int) (y - 1));
            p1 = colors.getPixel((int) (x + 1), (int) (y - 1));
            p2 = colors.getPixel((int) (x - 1), (int) (y + 1));
            p3 = colors.getPixel((int) (x + 1), (int) (y + 1));
            a = (((p0 >> 24) & 0xff) + ((p1 >> 24) & 0xff) + ((p2 >> 24) & 0xff) + ((p3 >> 24) & 0xff)) / 4;
            r = (((p0 >> 16) & 0xff) + ((p1 >> 16) & 0xff) + ((p2 >> 16) & 0xff) + ((p3 >> 16) & 0xff)) / 4;
            g = (((p0 >> 8) & 0xff) + ((p1 >> 8) & 0xff) + ((p2 >> 8) & 0xff) + ((p3 >> 8) & 0xff)) / 4;
            b = ((p0 & 0xff) + (p1 & 0xff) + (p2 & 0xff) + (p3 & 0xff)) / 4;
            return (a << 24) + (r << 16) + (g << 8) + b;
        }
        return colors.getPixel((int) x, (int) y);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
