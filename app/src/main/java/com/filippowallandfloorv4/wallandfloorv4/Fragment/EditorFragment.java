package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditorFragment extends android.app.Fragment {

    public ViewForDrawIn vfd;
    public TextView text;
    public Bitmap mBitmap;
    public Paint mPaint;

    public EditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return vfd;
    }

    public void setVfd(ViewForDrawIn vfd) {
        this.vfd = vfd;
    }

    public ViewForDrawIn getVfd() {
        return vfd;
    }

    public void setmBitmapAndPaint(Bitmap mBitmap,Paint mPaint){
        this.mBitmap = mBitmap;
        this.mPaint = mPaint;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
