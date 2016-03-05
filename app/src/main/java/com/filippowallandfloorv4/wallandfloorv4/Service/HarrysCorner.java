package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Filippo on 03/03/2016.
 */
public class HarrysCorner extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Mat cannyMat;
    private Bitmap originalBitmap;
    private ViewForDrawIn view;

    private int mCurrentY;
    private int mCurrentX;

    public HarrysCorner(Mat cannyMat, ViewForDrawIn view, Bitmap originalBitmap) {
        this.cannyMat = cannyMat;
        this.view = view;
        this.originalBitmap = originalBitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap hougtImage = Bitmap.createBitmap(cannyMat.width(),cannyMat.height(), Bitmap.Config.ARGB_8888);
        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        //Imgproc.cvtColor(cannyImageColor, reGrey, Imgproc.COLOR_RGB2GRAY);
        //FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.GFTT);
        //FeatureDetector detector = FeatureDetector.create(FeatureDetector.BRISK);
        //FeatureDetector detector = FeatureDetector.create(FeatureDetector.DENSE);
        detector.detect(cannyMat,keyPoint);
        Features2d.drawKeypoints(cannyMat, keyPoint, cannyMat);
        Utils.matToBitmap(cannyMat, hougtImage);
        return hougtImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setmBitmap(bitmap);
        view.invalidate();
        super.onPostExecute(bitmap);
    }
}
