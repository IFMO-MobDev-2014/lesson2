package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Interface for bitmap scaling.
 *
 * @author Zakhar Voit (zakharvoit@gmail.com)
 */
interface Scaler {
    Bitmap scale(Bitmap bitmap, float scale);
}
