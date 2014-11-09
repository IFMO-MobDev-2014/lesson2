package ru.ifmo.md.lesson2;

import android.graphics.Bitmap;

/**
 * Created by Anton Borzenko on 10.11.2014.
 */
public class ArrayImage {
    private int width, height;
    private int[] image;
    public ArrayImage(int[] image, int width, int height) {
        setImage(image, width, height);
    }
    public ArrayImage(int width, int height) {
        setImage(width, height);
    }
    public ArrayImage(Bitmap bitmap) {
        setImage(bitmap);
    }
    public int[] getImage() {
        return image;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void setImage(int[] image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }
    public void setImage(int width, int height) {
        setImage(new int[width * height], width, height);
    }
    public void setImage(Bitmap bitmap) {
        setImage(new int[width * height], bitmap.getWidth(), bitmap.getHeight());
        bitmap.getPixels(image, 0, width, 0, 0, width, height);
    }
}
