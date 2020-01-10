package edu.umb.cs.colorhistogram;

import android.graphics.Bitmap;

public class ImageBitmaps{
    private String name;

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap;

    public ImageBitmaps(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }
}
