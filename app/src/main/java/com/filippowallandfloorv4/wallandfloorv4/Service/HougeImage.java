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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

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

    private int threshold = 20;
    private int min_line_lenght = 50;
    private int max_line_gap = 5;

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
        // probalistic

        Imgproc.HoughLinesP(cannyMat, lines, 1, Math.PI / 180, threshold, min_line_lenght, max_line_gap);
        Imgproc.cvtColor(cannyMat, cannyImageColor, Imgproc.COLOR_GRAY2RGB);
        for (int i = 0; i < lines.rows();i++){

            double rho = lines.get(i,0)[0];
            double theta = lines.get(i,0)[1];
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a+rho;
            double y0 = b+rho;


            double line[] = lines.get(i,0);
            double  xStart = line[0],//Math.round(x0 + 1000*(-b)), //line[0],
                    yStart = line[1],// Math.round(y0 + 1000*(a)), //line[1],
                    xEnd =  line[2],//Math.round(x0 - 1000*(-b)),//line[2],
                    yEnd = line[3];// Math.round(y0 - 1000*(a)); //line[3];

            org.opencv.core.Point lineStart = new org.opencv.core.Point(xStart,yStart);
            org.opencv.core.Point lineEnd = new org.opencv.core.Point(xEnd,yEnd);
            Imgproc.line(cannyImageColor,lineStart,lineEnd,new Scalar(0,0,255),3);
        }
        // not probabilistic
        /**
        Imgproc.HoughLines(cannyMat,lines,2,Math.PI/180,threshold);
        Imgproc.cvtColor(cannyMat, cannyImageColor, Imgproc.COLOR_GRAY2RGB);
        for (int i = 0 ; i<lines.rows();i++){
            double rho = lines.get(i,0)[0];
            double theta = lines.get(i,0)[1];
            Point pt1 = new Point();
            Point pt2 = new Point();
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a+rho;
            double y0 = b+rho;
            pt1.x = Math.round(x0 + 1000*(-b));
            pt1.y = Math.round(y0 + 1000*(a));
            pt2.x = Math.round(x0 - 1000*(-b));
            pt2.y = Math.round(y0 - 1000*(a));

            Imgproc.line(cannyImageColor,pt1,pt2,new Scalar(0,0,255),3);
        }*/
        /**ArrayList<Point>corners = new ArrayList<Point>();
        for (int i=0; i<lines.rows();i++){
            for (int j= i+1;j<lines.rows();j++ ){
                Point intersectionPoint = getLinesIntersection(lines.get(i,0),lines.get(j,0));
                if (intersectionPoint != null){
                    corners.add(intersectionPoint);
                }
            }
        }
        /**MatOfPoint2f cornersMat = new MatOfPoint2f();
        cornersMat.fromList(corners);
        Log.e("cornersMat: ", "cornersMat: " + cornersMat);
        MatOfPoint2f approxCorner = new MatOfPoint2f();
        Imgproc.approxPolyDP(cornersMat, approxCorner, Imgproc.arcLength(cornersMat, true) * 0.02, false);
        Log.e("cornersMatApprox: ", "cornersMatApprox: " + approxCorner);
        for (Point point : approxCorner.toList()){
            Imgproc.circle(cannyImageColor,point,5,new Scalar(0,255,0),3);
            Log.e("IntersectionPoint", "Point: " + point.x +" "+point.y);
        }*/
        /**for (Point point : corners){
            Imgproc.circle(cannyImageColor,point,5,new Scalar(0,255,0),3);
        }*/
        Utils.matToBitmap(cannyImageColor, hougtImage);
        return hougtImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setmBitmap(bitmap);
        view.invalidate();
        super.onPostExecute(bitmap);
    }
    private org.opencv.core.Point getLinesIntersection(double [] firstLine, double [] secondLine)
    {
        double FX1=firstLine[0],FY1=firstLine[1],FX2=firstLine[2],FY2=firstLine[3];
        double SX1=secondLine[0],SY1=secondLine[1],SX2=secondLine[2],SY2=secondLine[3];
        org.opencv.core.Point intersectionPoint=null;
        //Make sure the we will not divide by zero
        double denominator=(FX1-FX2)*(SY1-SY2)-(FY1-FY2)*(SX1-SX2);
        if(denominator!=0)
        {
            intersectionPoint=new org.opencv.core.Point();
            intersectionPoint.x=((FX1*FY2-FY1*FX2)*(SX1-SX2)-(FX1-FX2)*(SX1*SY2-SY1*SX2))/denominator;
            intersectionPoint.y=((FX1*FY2-FY1*FX2)*(SY1-SY2)-(FY1-FY2)*(SX1*SY2-SY1*SX2))/denominator;
            if(intersectionPoint.x < 0 || intersectionPoint.y < 0)
                return null;
        }
        return intersectionPoint;
    }
}
