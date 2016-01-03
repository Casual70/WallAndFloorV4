package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
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
public class PrepareImage extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Bitmap originalBitmap;
    private Bitmap backBitmap;
    private ViewForDrawIn view;
    private WafImage wafImage;

    private double threshold_min = 20;


    public PrepareImage(Bitmap originalBitmap, ViewForDrawIn view) {
        this.originalBitmap = originalBitmap;
        this.view = view;
        this.wafImage = view.getWafImage();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // dialog
        dialogSetBitmap();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        //CannyEdgeDetector detector = new CannyEdgeDetector();
        //detector.setLowThreshold(1.0f);
        //detector.setHighThreshold(2.5f);
        //detector.setSourceImage(originalBitmap);
       // detector.process();
        Bitmap edgeImage = Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Mat imageOriginalMat = new Mat();
        Utils.bitmapToMat(originalBitmap,imageOriginalMat);
        Mat imageCanny  = new Mat();
        Mat imageGray = new Mat();
        Imgproc.cvtColor(imageOriginalMat,imageGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(imageGray, imageCanny, new Size(3, 3));
        Imgproc.Canny(imageCanny, imageCanny, threshold_min, threshold_min * 3, 3, true);
        Utils.matToBitmap(imageCanny, edgeImage);
        return edgeImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setBackBitmap(bitmap);
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
        final Dialog dialog = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        final Bitmap bitmap = originalBitmap;
        View view = LayoutInflater.from(this.view.getContext()).inflate(R.layout.edge_detector_accuracy,null);
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
                dialog.dismiss();
                onPostExecute(doInBackground(bitmap));
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}
