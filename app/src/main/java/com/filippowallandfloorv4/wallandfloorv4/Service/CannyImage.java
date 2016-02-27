package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/**
 * Created by Filippo on 16/12/2015.
 */
public class CannyImage extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Bitmap originalBitmap;
    private Bitmap backBitmap;
    private ViewForDrawIn view;

    private int mCurrentY;
    private int mCurrentX;

    private double threshold_min = 20;


    public CannyImage(Bitmap originalBitmap, ViewForDrawIn view) {
        this.originalBitmap = originalBitmap;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // dialog
        dialogSetBitmap();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap edgeImage = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Mat imageOriginalMat = new Mat();
        Utils.bitmapToMat(originalBitmap,imageOriginalMat);
        Mat imageCanny  = new Mat();
        Mat imageGray = new Mat();
        Imgproc.cvtColor(imageOriginalMat,imageGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(imageGray, imageCanny, 5);
        Imgproc.Canny(imageCanny, imageCanny, threshold_min, threshold_min * 15, 5, true);
        Utils.matToBitmap(imageCanny, edgeImage);
        return edgeImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setBackBitmap(bitmap);
        view.setBackCanvas(new Canvas(bitmap));
        view.setmBitmap(originalBitmap);
        view.invalidate();
        super.onPostExecute(bitmap);
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmap) {
        view.setmBitmap(bitmap[0]);
        view.invalidate();
        super.onProgressUpdate(bitmap[0]);
    }

    private void dialogSetBitmap(){
        final Bitmap bitmap = originalBitmap;
        final View view = LayoutInflater.from(this.view.getContext()).inflate(R.layout.edge_detector_accuracy,null);
        final PopupWindow pop = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        SeekBar contrast = (SeekBar)view.findViewById(R.id.seekEdgeDetector);
        threshold_min = contrast.getProgress();
        contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshold_min = progress;
                onProgressUpdate(doInBackground(bitmap));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ImageButton saveImage = (ImageButton)view.findViewById(R.id.SaveDetectorAccuracy);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                onPostExecute(doInBackground(bitmap));
                cancel(true);
            }
        });
        ImageButton drag = (ImageButton)view.findViewById(R.id.dragButton);
        View.OnTouchListener otl = new View.OnTouchListener() {
            private float mDx;
            private float mDy;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mDx = mCurrentX - event.getRawX();
                    mDy = mCurrentY - event.getRawY();
                } else
                if (action == MotionEvent.ACTION_MOVE) {
                    mCurrentX = (int) (event.getRawX() + mDx);
                    mCurrentY = (int) (event.getRawY() + mDy);
                    pop.update(mCurrentX, mCurrentY, -1, -1);
                }
                return true;
            }
        };
        //mCurrentX = (this.view.getWidth()/2 - (pop.getWidth()/2));
        //mCurrentY = (this.view.getHeight()/2 - (pop.getHeight()/2));
        mCurrentX = this.view.getWidth()/8;
        mCurrentY = this.view.getHeight()/2;

        drag.setOnTouchListener(otl);
        view.post(new Runnable() {
            @Override
            public void run() {
                pop.showAtLocation(view, Gravity.NO_GRAVITY, mCurrentX, mCurrentY);
            }
        });
    }
}
