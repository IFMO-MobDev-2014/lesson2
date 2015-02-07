package freemahn.com.lesson2;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends Activity {

    public static final float SCALE_SIZE = 1.73F;
    public static final float SCALE_COLOR = 2.0F;
    Bitmap bitmap;
    Bitmap bitmapFast;
    Bitmap bitmapSlow;

    Bitmap rotateAndBright(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] source = new int[width * height];
        int[] result = new int[width * height];
        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int newColor = source[i + j * width];

                int newRed = (newColor >> 16) & 0xFF;
                int newGreen = (newColor >> 8) & 0xFF;
                int newBlue = newColor & 0xFF;

                newRed = Math.min(0xFF, (int) (newRed * SCALE_COLOR));
                newGreen = Math.min(0xFF, (int) (newGreen * SCALE_COLOR));
                newBlue = Math.min(0xFF, (int) (newBlue * SCALE_COLOR));

                newColor = (newRed << 16) | (newGreen << 8) | newBlue;
                result[i * height + (height - j - 1)] = newColor;
            }
        }

        return Bitmap.createBitmap(result, height, width, Bitmap.Config.RGB_565);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ImageView view = new ImageView(getApplicationContext());
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.source);
        scaleFast();
        scaleSlow();
        bitmapFast = rotateAndBright(bitmapFast);
        bitmapSlow = rotateAndBright(bitmapSlow);
        view.setImageBitmap(bitmapFast);

        view.setOnClickListener(new View.OnClickListener() {
            boolean fast = false;

            @Override
            public void onClick(View v) {
                view.setImageBitmap(fast ? bitmapFast : bitmapSlow);
                fast = !fast;
            }
        });

        view.setScaleType(ImageView.ScaleType.CENTER);
        setContentView(view);
    }

    public void scaleSlow() {
        int newWidth = (int) ((bitmap.getWidth() - 1) / SCALE_SIZE + 1);
        int newHeight = (int) ((bitmap.getHeight() - 1) / SCALE_SIZE + 1);

        int[] source = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int[] red = new int[newWidth * newHeight];
        int[] green = new int[newWidth * newHeight];
        int[] blue = new int[newWidth * newHeight];
        int[] count = new int[newWidth * newHeight];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int newI = (int) (i / SCALE_SIZE);
                int newJ = (int) (j / SCALE_SIZE);
                int pos = newI + newWidth * newJ;
                int color = bitmap.getPixel(i, j);

                red[pos] += (color >> 16) & 0xFF;
                green[pos] += (color >> 8) & 0xFF;
                blue[pos] += color & 0xFF;
                count[pos]++;
            }
        }

        int[] result = new int[newWidth * newHeight];
        for (int pos = 0; pos < newWidth * newHeight; pos++) {
            red[pos] /= count[pos];
            green[pos] /= count[pos];
            blue[pos] /= count[pos];
            result[pos] = (red[pos] << 16) | (green[pos] << 8) | blue[pos];
        }

        bitmapSlow = Bitmap.createBitmap(result, newWidth, newHeight, Bitmap.Config.RGB_565);
    }

    public void scaleFast() {
        int newWidth = (int) ((bitmap.getWidth() - 1) / SCALE_SIZE + 1);
        int newHeight = (int) ((bitmap.getHeight() - 1) / SCALE_SIZE + 1);
        int[] source = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] result = new int[newWidth * newHeight];
        bitmap.getPixels(source, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result[i + j * newWidth] = source[(int) (i * SCALE_SIZE) + bitmap.getWidth() * ((int) (j * SCALE_SIZE))];
            }
        }
        bitmapFast = Bitmap.createBitmap(result, newWidth, newHeight, Bitmap.Config.RGB_565);
    }
}