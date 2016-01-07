package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

/**
 * Created by Filippo on 07/01/2016.
 */
public class UndoRedoBitmap extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private Bitmap originalBitmap;
    private ViewForDrawIn view;

    public UndoRedoBitmap(Bitmap originalBitmap, ViewForDrawIn view) {
        this.originalBitmap = originalBitmap;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }
}
