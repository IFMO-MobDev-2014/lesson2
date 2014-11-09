package ru.ifmo.md.lesson2;

/**
 * Created by Anton Borzenko on 10.11.2014.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.BitmapFactory;

public class ImageView extends SurfaceView {
    private static final float SIZE_SCALE = 1.73f;
    private static final float BRIGHTNESS_RATIO = 2.0f;
    private SurfaceHolder holder;
    private boolean isGood = true;
    private ArrayImage goodImage = null, badImage = null;
    public ImageView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {}
            public void surfaceDestroyed(SurfaceHolder holder) {}
            public void surfaceCreated(SurfaceHolder holder) {
                drawPicture();
            }
        });
        setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                swapPictures();
            }
        });
        createPictures();
    }

    private void createPictures() {
        ArrayImage imageFromResource = new ArrayImage(BitmapFactory.decodeResource(getResources(), R.drawable.source));
        goodImage = ImageEditor.scale(imageFromResource, 1.0 / SIZE_SCALE, ImageEditor.Mode.SLOW);
        goodImage = ImageEditor.rotate(goodImage, 1);
        goodImage = ImageEditor.changeBrightness(goodImage, BRIGHTNESS_RATIO);

        badImage = ImageEditor.scale(imageFromResource, 1.0f / SIZE_SCALE, ImageEditor.Mode.FAST);
        badImage = ImageEditor.rotate(badImage, 1);
        badImage = ImageEditor.changeBrightness(badImage, BRIGHTNESS_RATIO);
    }

    private void swapPictures() {
        isGood = !isGood;
        drawPicture();
    }
    private void drawPicture() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            onDraw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
    @Override
    public void onDraw(Canvas canvas) {
        ArrayImage cur = isGood ? goodImage : badImage;
        if (cur != null) {
            canvas.drawBitmap(cur.getImage(), 0, cur.getWidth(), 0, 0, cur.getWidth(), cur.getHeight(), false, null);
        }
    }
}
