package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RotateActivity extends Activity {

    private RotateHelper helper;
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

    public void onRSBClick(View view) {
        // rotate, scale and brighten the picture
        new RotateTask().execute();
    }

    public void onImageClick(View view) {
        toggleScaleMode();
    }

    public void toggleScaleMode() {
        scaleMode = !scaleMode;
        if (scaleMode) {
            scaleModeView.setText(R.string.scale_fast);
            scaleModeView.setTextColor(Color.RED);
        } else {
            scaleModeView.setText(R.string.scale_best);
            scaleModeView.setTextColor(Color.BLUE);
        }
    }

    private class RotateTask extends AsyncTask<Void, Void, long[]> {
        protected long[] doInBackground(Void... params) {
            long[] times = new long[3];
            SurfaceHolder holder = rotateView.getHolder();
            if (holder.getSurface().isValid()) {
                Canvas canvas = holder.lockCanvas();
                helper = new RotateHelper(source);
                helper.setScaleMode(scaleMode);
                helper.scale(1.73f, 1.73f);
                helper.rotateCW90();
                helper.brighten(2.0f);
                rotateView.setSize(helper.getWidth(), helper.getHeight());
                rotateView.setPixels(helper.getPixels());
                rotateView.drawIt(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            times[0] = helper.scaleTime;
            times[1] = helper.rotateTime;
            times[2] = helper.brightenTime;
            return times;
        }

        protected void onPostExecute(long[] times) {
            // display toast with useful information
            String scaleTimeMsg = getString(R.string.dialog_scale_time) + " = " + times[0] + getString(R.string.dialog_ms);
            String rotateTimeMsg = getString(R.string.dialog_rotate_time) + " = " + times[1] + getString(R.string.dialog_ms);
            String brightenTimeMsg = getString(R.string.dialog_brighten_time) + " = " + times[2] + getString(R.string.dialog_ms);
            String toastText = scaleTimeMsg + "\n" + rotateTimeMsg + "\n" + brightenTimeMsg;
            Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }
}
