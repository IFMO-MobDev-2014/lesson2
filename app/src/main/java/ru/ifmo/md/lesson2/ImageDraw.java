package ru.ifmo.md.lesson2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by igorpanshin on 06.10.14.
 */

//Класс обеспечивающий вывод изображения и взаимодействие с пользователем
public class ImageDraw  extends SurfaceView {

    //Класс обработчик обратного вызова изменения состояния surface
    private class SurfaceCreatedCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {}
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawExample();
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {}
    }

    //Класс обработчик события щелкчка
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            swapDrawMode();
        }
    };

    SurfaceHolder holder;
    public ImageDraw(Context context) {
        super(context);
        holder = getHolder();

        //Устанавливаем калбек на изменение состояния surface,
        //чтобы выполнить первичную отрисовку после его создания
        holder.addCallback(new SurfaceCreatedCallback());

        //Подписываемся на событие щелчка по экрану
        this.setOnClickListener(onClickListener);
    }

    private boolean useFastDraw = true;
    private void swapDrawMode() {
        useFastDraw = !useFastDraw;
        drawExample();
    }

    public void drawExample() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            onDraw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Подгрузка картинки из ресурсов
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.source);

        //Мы должны быть уверены, что каждый канал кодируется 8 битами
        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);

        //Получаем ширину и высоту изображения и выделяем под него массив
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] image = new int[w * h];

        //Заполняем массив из изображения
        bmp.getPixels(image, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        //Масштабируем изображение исходя из текущего режима
        if(useFastDraw) {
            image = ImageEditor.scaleFast(image, w, 1.73f);
        }
        else {
            image = ImageEditor.scaleGood(image, w, 1.73f);
        }

        //Соответственно пересчитываем его размер
        w = Math.round(w / 1.73f);
        h = Math.round(h / 1.73f);

        //Увеличиваем яркость
        //Значение соответствующее "раза в 2" получен эмпирическим путём
        image = ImageEditor.addBrightness(image, w, 75);

        //Поворачиваем изображение на 90°
        image = ImageEditor.rotateImageBy90(image, w);
        int oldw = w;
        w = h; h = oldw;

        //И выводим результат на экран
        canvas.drawBitmap(image, 0, w, 0, 0, w, h, false, null);
    }



}
