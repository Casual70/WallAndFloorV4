package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.os.AsyncTask;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;

public class ProspectTexture extends AsyncTask<Bitmap,Void,Bitmap> {

    private Bitmap plateBitmap;
    private Bitmap prospectBitmap;
    private ViewForDrawIn vfd;
    private ArrayList<Point>corners;

    public ProspectTexture(Bitmap plateBitmap, ViewForDrawIn vfd ,ArrayList<Point>corners) {
        this.plateBitmap = plateBitmap;
        this.vfd = vfd;
        this.corners = corners;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap returnBit = Bitmap.createBitmap(plateBitmap);
        Mat originalTexture = new Mat();
        Utils.bitmapToMat(plateBitmap,originalTexture);
        Mat correctTexture = new Mat(originalTexture.rows(),originalTexture.cols(),originalTexture.type());
        ArrayList<Point> originalCorner = new ArrayList<>();
        Point one = new Point(0,0);
        Point two = new Point(originalTexture.cols(),0);
        Point tree = new Point(originalTexture.cols(),originalTexture.rows());
        Point four = new Point(0,originalTexture.rows());
        originalCorner.add(one);originalCorner.add(two);originalCorner.add(tree);originalCorner.add(four);
        Mat originalCornersMat = Converters.vector_Point2f_to_Mat(originalCorner);
        Mat correctCornerMat = Converters.vector_Point2f_to_Mat(corners);
        Mat trasformation = Imgproc.getPerspectiveTransform(originalCornersMat,correctCornerMat); // vedere se invertire
        Imgproc.warpPerspective(originalTexture, correctTexture, trasformation, correctTexture.size());
        Utils.matToBitmap(correctTexture, returnBit);
        return returnBit;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        BitmapShader texture = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        vfd.getmPaint().setShader(texture);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
