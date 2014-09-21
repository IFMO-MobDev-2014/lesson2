package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

public class RotateActivity extends Activity {
    private RotateView rotateView;
    private TextView scaleModeView;
    private Bitmap source;

    private boolean scaleMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);

        // find view elements and resources by their id
        rotateView = (RotateView) findViewById(R.id.rotateView);
        scaleModeView = (TextView) findViewById(R.id.scaleMode);
        source = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        toggleScaleMode();
    }

    public void onDrawClick(View view) {
        // draw initial picture
        SurfaceHolder holder = rotateView.getHolder();
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            RotateHelper helper = new RotateHelper(source);
            rotateView.setSize(helper.getWidth(), helper.getHeight());
            rotateView.setPixels(helper.getPixels());
            rotateView.drawIt(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void onRSBClick(View view) {
        // rotate, scale and brighten the picture
        SurfaceHolder holder = rotateView.getHolder();
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            RotateHelper helper = new RotateHelper(source);
            helper.setScaleMode(scaleMode);
            helper.scale(1.73f, 1.73f);
            helper.rotateCW90();
            helper.brighten(2.0f);
            rotateView.setSize(helper.getWidth(), helper.getHeight());
            rotateView.setPixels(helper.getPixels());
            rotateView.drawIt(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void onImageClick(View view) {
        toggleScaleMode();
    }

    public void toggleScaleMode() {
        scaleMode = !scaleMode;
        if (scaleMode) {
            scaleModeView.setText("fast scale");
            scaleModeView.setTextColor(Color.RED);
        } else {
            scaleModeView.setText("accurate scale");
            scaleModeView.setTextColor(Color.BLUE);
        }
    }
}
