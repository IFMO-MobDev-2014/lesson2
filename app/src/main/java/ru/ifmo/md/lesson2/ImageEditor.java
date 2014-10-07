package ru.ifmo.md.lesson2;

/**
 * Created by Y on 07.10.14.
 */
//Статический класс реализующий требуемые методы обработки изображения
public class ImageEditor {

    //Поворот изображения на 90° транспонированием и отражением
    public static int[] rotateImageBy90(int[] img, int width){
        int height = img.length / width;
        int[] newImg = new int[img.length];
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++) {
                newImg[y + (width - x - 1)*height] = img[x + y*width];
            }
        return newImg;
    }

    //Функция получения значения каналов ARGB
    private static int[] getARGB(int argb){
        int b = (argb) & 0x000000FF;
        int g = (argb >> 8) & 0x000000FF;
        int r = (argb >> 16) & 0x000000FF;
        int a = (argb >> 24) & 0x000000FF;
        return new int[] {a, r, g, b};
    }

    //Вспомагательная функция усиления яркости канала на заданное число
    private static int channelAdd(int a, int b){
        return Math.round(a + (float) (255 - a) / 255 * b);
    }

    //Функция увеличения яркости изображения на заданное число
    public static int[] addBrightness(int[] img, int width, int amount){
        int[] newImg = new int[img.length];
        int height = img.length / width;
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++) {
                int argb = img[x + y * width];
                int[] channels = getARGB(argb);

                int a = channels[0];
                int r = channels[1];
                int g = channels[2];
                int b = channels[3];

                r = channelAdd(r, amount);
                g = channelAdd(g, amount);
                b = channelAdd(b, amount);

                int newArgb = b | g << 8 | r << 16 | a << 24;
                newImg[x + y*width] = newArgb;
            }
        return newImg;
    }

    //Быстрое уменьшение масштаба изображения в заданное число раз.
    public static int[] scaleFast(int[] img, int width, float factor){
        if(factor < 1) factor = 1 / factor;
        int height = img.length / width;
        int w = Math.round(width / factor);
        int h = Math.round(height / factor);
        int[] newImg = new int[w * h];
        for(int y = 0; y < h; y++)
            for(int x = 0; x < w; x++) {
                int originX = Math.round(x * factor);
                int originY = Math.round(y * factor);
                newImg[x + y * w] = img[originX + originY * width];
            }
        return newImg;
    }

    //Алгоритм уменьшения масштаба приличного качества
    //Решено брать среднее арифметическое точек, сложивжихся в один пиксель
    public static int[] scaleGood(int[] img, int width, float factor) {
        if(factor < 1) factor = 1 / factor;
        int height = img.length / width;
        int w = Math.round(width / factor);
        int h = Math.round(height / factor);
        int[] newImg = new int[w * h];

        //Для начала проссумируем все точки попадающие в один пиксель
        //Первые три элемента - сумма цветов ARGB, последний - количество точек
        int[][][] tempSum = new int[w][h][5];
        for(int originY = 0; originY < height; originY++)
            for(int originX = 0; originX < width; originX++) {
                int x = Math.round(originX / factor);
                int y = Math.round(originY / factor);

                int argb = img[originX + originY * width];
                int[] channels = getARGB(argb);

                int[] sum = tempSum[x][y];
                for(int i = 0; i < 4; i++)
                    sum[i] += channels[i];
                sum[4]++;
            }

        //Теперь, зная сумму цветов и число точек, создадим новое изображение
        //расчитав среднее арифметическое.
        for(int y = 0; y < h; y++)
            for(int x = 0; x < w; x++) {
                int pointsCount = tempSum[x][y][4];
                for(int i = 0; i < 4; i++)
                    tempSum[x][y][i] = tempSum[x][y][i] / pointsCount;

                int a = tempSum[x][y][0];
                int r = tempSum[x][y][1];
                int g = tempSum[x][y][2];
                int b = tempSum[x][y][3];

                int res = b | g << 8 | r << 16 | a << 24;
                newImg[x + y * w] = res;
            }
        return newImg;
    }
}
