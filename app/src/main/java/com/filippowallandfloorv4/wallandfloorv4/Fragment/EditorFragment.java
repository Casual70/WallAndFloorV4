package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
        Log.e("fragment","OncreateView");
        View view = inflater.inflate(R.layout.prova2,null);
        vfd = (ViewForDrawIn)view.findViewById(R.id.ViewForDrawIn);
        vfd.setmBitmap(mBitmap);
        vfd.setmPaint(mPaint);
        text = (TextView)view.findViewById(R.id.TextViewProva);
        return view;
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
