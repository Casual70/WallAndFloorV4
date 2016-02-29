package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Filippo on 27/02/2016.
 */
public class HougeImage extends AsyncTask <Bitmap,Bitmap,Bitmap>{

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Mat cannyMat;
    private Bitmap originalBitmap;
    private ViewForDrawIn view;

    private int mCurrentY;
    private int mCurrentX;

    private int threshold = 7;
    private int min_line_lenght = 3;
    private int max_line_gap = 7;

    public HougeImage(Mat cannyMat, ViewForDrawIn view, Bitmap originalBitmap) {
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
        Mat cannyImageColor = new Mat();
        Mat lines = new Mat();
        Imgproc.HoughLinesP(cannyMat, lines, 1, Math.PI / 180, threshold, min_line_lenght, max_line_gap);
        Imgproc.cvtColor(cannyMat, cannyImageColor, Imgproc.COLOR_GRAY2RGB);
        for (int i = 0; i < lines.rows();i++){
            double line[] = lines.get(i,0);
            double xStart = line[0],
                    yStart = line[1],
                    xEnd = line[2],
                    yEnd = line[3];
            org.opencv.core.Point lineStart = new org.opencv.core.Point(xStart,yStart);
            org.opencv.core.Point lineEnd = new org.opencv.core.Point(xEnd,yEnd);
            Imgproc.line(cannyImageColor,lineStart,lineEnd,new Scalar(0,0,255),3);
        }
        Utils.matToBitmap(cannyImageColor, hougtImage);
        return hougtImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setmBitmap(bitmap);
        view.invalidate();
        super.onPostExecute(bitmap);
    }
}
