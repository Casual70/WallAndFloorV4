package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

/**
 * Created by Filippo on 16/12/2015.
 */
public class PrepareImage extends AsyncTask<Bitmap,Void,Bitmap> {

    private final static String PrepareImage_Log = "PrepareImage_log";

    private Bitmap originalBitmap;
    private Paint mPaint;
    private Bitmap backBitmap;

    public PrepareImage(Bitmap originalBitmap, Paint mPaint, View v) {
        this.originalBitmap = originalBitmap;
        this.mPaint = mPaint;
        this.backBitmap = Bitmap.createBitmap(originalBitmap);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        int h = originalBitmap.getHeight();
        int w = originalBitmap.getWidth();
        Log.e(PrepareImage_Log, "h: " + h + " " + "w: " + w);
        for (int indH = 1; indH < h-2; indH++){
            for (int indW = 1; indW < w-2; indW++){
                int startPix = originalBitmap.getPixel(indW,indH);
                int Rstart,Gstart,Bstart;
                Rstart = Color.red(startPix);
                Gstart = Color.green(startPix);
                Bstart = Color.blue(startPix);
                int leftPix = originalBitmap.getPixel(indW+1,indH);
                int Rleft,Gleft,Bleft;
                Rleft = Color.red(leftPix);
                Gleft = Color.green(leftPix);
                Bleft = Color.blue(leftPix);
                int downPix = originalBitmap.getPixel(indW,indH+1);
                int Rdown,Gdown,Bdown;
                Rdown = Color.red(downPix);
                Gdown = Color.green(downPix);
                Bdown = Color.blue(downPix);

                double distLeft = Math.sqrt((Rstart-Rleft)*(Rstart-Rleft)+(Gstart-Gleft)*(Gstart-Gleft)+(Bstart-Bleft)*(Bstart-Bleft));
                double distDown = Math.sqrt((Rstart-Rdown)*(Rstart-Rdown)+(Gstart-Gdown)*(Gstart-Gdown)+(Bstart-Bdown)*(Bstart-Bdown));
                Log.e(PrepareImage_Log,"distLeft: "+distLeft + " "+ "distDown: "+distDown);

                if (distLeft>20||distDown>20){ // il parametro potrà essere settato dall'utente
                    backBitmap.setPixel(indW,indH,mPaint.getColor());
                }else{
                    backBitmap.setPixel(indW,indH,Color.rgb(255,255,255));
                }
            }
        }
        return backBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    public Bitmap getBackBitmap() {
        return backBitmap;
    }
}
