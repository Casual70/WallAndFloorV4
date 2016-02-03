package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.filippowallandfloorv4.wallandfloorv4.Model.Mypixel;

import java.util.LinkedList;

/**
 * Created by Filippo on 20/12/2015.
 */
public class FloodFill {

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap backBitmap;
    private Bitmap outputBitmap;
    private Mypixel nestMypixel;

    public FloodFill(Canvas mCanvas, Paint mPaint, Bitmap backBitmap, Mypixel nestMypixel) {
        this.mCanvas = mCanvas;
        this.mPaint = mPaint;
        this.backBitmap = backBitmap;
        this.nestMypixel = nestMypixel;
    }

    public void fillY(){
        Bitmap bitmap = backBitmap;
        Mypixel nestP = nestMypixel;
        int y1 = nestP.y;
        LinkedList<Mypixel> pixelsY1 = new LinkedList<>();
        while (y1< bitmap.getHeight()-1 && bitmap.getPixel(nestP.x, y1)== Color.BLACK){
            Mypixel p = new Mypixel(nestP.x,y1, bitmap.getPixel(nestP.x, y1));
            pixelsY1.add(p);
            y1++;
        }
        int y2 = nestP.y;
        LinkedList<Mypixel>pixelsY2 = new LinkedList<>();
        while (y2>1 && bitmap.getPixel(nestP.x, y2)== Color.BLACK){
            Mypixel p = new Mypixel(nestP.x,y2, bitmap.getPixel(nestP.x, y2));
            pixelsY2.add(p);
            y2--;
        }
        Path path = new Path();
        path.moveTo(pixelsY2.getLast().x, pixelsY2.getLast().y);
        path.lineTo(pixelsY1.getLast().x, pixelsY1.getLast().y);
        mCanvas.drawPath(path, mPaint);
    }
    public void fillX(){
        Bitmap bitmap = backBitmap;
        Mypixel nestP = nestMypixel;
        int x1 = nestP.x;
        LinkedList<Mypixel> pixelsY1 = new LinkedList<>();
        while (x1< bitmap.getWidth()-1 && bitmap.getPixel(x1 ,nestP.y)== Color.BLACK){
            Mypixel p = new Mypixel(x1,nestP.y, bitmap.getPixel(x1,nestP.y));
            pixelsY1.add(p);
            x1++;
        }
        int x2 = nestP.x;
        LinkedList<Mypixel>pixelsY2 = new LinkedList<>();
        while (x2>1 && bitmap.getPixel(x2 ,nestP.y)== Color.BLACK){
            Mypixel p = new Mypixel(x2 ,nestP.y, bitmap.getPixel(x2 ,nestP.y));
            pixelsY2.add(p);
            x2--;
        }
        Path path = new Path();
        path.moveTo(pixelsY2.getLast().x, pixelsY2.getLast().y);
        path.lineTo(pixelsY1.getLast().x, pixelsY1.getLast().y);

        mCanvas.drawPath(path, mPaint);
    }
}
