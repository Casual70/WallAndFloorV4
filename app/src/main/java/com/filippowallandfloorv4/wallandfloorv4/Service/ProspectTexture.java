package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ProspectTexture extends AsyncTask<Bitmap,Void,Bitmap> {

    private Bitmap plateBitmap;
    private Bitmap prospectBitmap;
    private ViewForDrawIn vfd;

    public ProspectTexture(Bitmap plateBitmap, ViewForDrawIn vfd) {
        this.plateBitmap = plateBitmap;
        this.vfd = vfd;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap edgeImage = Bitmap.createBitmap(plateBitmap.getWidth(),plateBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Mat imageOriginalMat = new Mat();
        Utils.bitmapToMat(params[0], imageOriginalMat);
        Mat imagProsc = new Mat();
        Imgproc.getPerspectiveTransform(imageOriginalMat,imagProsc);

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
