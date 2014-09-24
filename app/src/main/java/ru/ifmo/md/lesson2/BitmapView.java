package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by flash on 24.09.14.
 */
public class BitmapView extends ImageView {

    private Bitmap sourceBackground;

    private boolean isFast;

    public BitmapView(Context context) {
        super(context);
        BitmapFactory.Options options = new BitmapFactory.Options();
        sourceBackground = BitmapFactory.decodeResource(getResources(), R.drawable.source, options);
        isFast = true;
        Bitmap newBitmap = new BitmapEffects(sourceBackground).compressFast(1.73).rotate().lighten().getBitmap();
        setImageBitmap(newBitmap);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap newBitmap;
                if (isFast) {
                    newBitmap = new BitmapEffects(sourceBackground).compressSlow(1.73).rotate().lighten().getBitmap();
                    isFast = false;
                } else {
                    newBitmap = new BitmapEffects(sourceBackground).compressFast(1.73).rotate().lighten().getBitmap();
                    isFast = true;
                }
                setImageBitmap(newBitmap);
            }
        });
    }


}
