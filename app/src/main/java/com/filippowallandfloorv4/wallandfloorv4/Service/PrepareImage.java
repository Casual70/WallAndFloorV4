package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

/**
 * Created by Filippo on 16/12/2015.
 */
public class PrepareImage extends AsyncTask<Bitmap,Void,Bitmap> {

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Bitmap originalBitmap;
    private Bitmap backBitmap;
    private ViewForDrawIn view;


    public PrepareImage(Bitmap originalBitmap, ViewForDrawIn view) {
        this.originalBitmap = originalBitmap;
        this.view = view;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // dialog
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        CannyEdgeDetector detector = new CannyEdgeDetector();
        detector.setLowThreshold(1.0f);
        detector.setHighThreshold(1.2f);
        detector.setSourceImage(originalBitmap);
        detector.process();
        Bitmap edgeImage = detector.getEdgesImage();
        return edgeImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setBackBitmap(bitmap);
        //view.setmBitmap(bitmap);
        view.invalidate();
        super.onPostExecute(bitmap);
        //dialog close
    }
}
