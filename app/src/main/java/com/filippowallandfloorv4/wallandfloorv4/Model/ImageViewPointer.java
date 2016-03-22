package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Filippo on 20/03/2016.
 */
public class ImageViewPointer extends ImageView {

    private float x;
    private float y;

    public ImageViewPointer(Context context,float x, float y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }
}
