package ru.ifmo.md.lesson2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

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
            scaleModeView.setText("fast scale");
            scaleModeView.setTextColor(Color.RED);
        } else {
            scaleModeView.setText("accurate scale");
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
            // display message box
            AlertDialog ad = new AlertDialog.Builder(RotateActivity.this).create();
            ad.setCancelable(false);
            String scaleTimeMsg = "scale time = " + times[0] + "ms";
            String rotateTimeMsg = "rotate time = " + times[1] + "ms";
            String brightenTimeMsg = "brighten time = " + times[2] + "ms";
            ad.setMessage(scaleTimeMsg + "\n" + rotateTimeMsg + "\n" + brightenTimeMsg);
            ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            ad.show();
        }
    }
}
