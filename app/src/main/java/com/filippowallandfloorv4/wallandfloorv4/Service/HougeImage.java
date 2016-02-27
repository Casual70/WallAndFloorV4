package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

/**
 * Created by Filippo on 27/02/2016.
 */
public class HougeImage extends AsyncTask <Bitmap,Bitmap,Bitmap>{

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Bitmap originalBitmap;
    private Bitmap backBitmap;
    private ViewForDrawIn view;

    private int mCurrentY;
    private int mCurrentX;

    private double threshold_min = 20;

    public HougeImage(Bitmap originalBitmap, ViewForDrawIn view) {
        this.originalBitmap = originalBitmap;
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
    }
}
