package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.filippowallandfloorv4.wallandfloorv4.Service.CannyEdgeDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewForDrawIn extends View {

    public Paint mPaint;
    public Paint currentPaint;

    private static final String VFD_LOG = "ViewForDrawIn_Debug";
    private static final String AutoFinder_debug = "AutoFinder_Debug";

    private Bitmap mBitmap;         //
    private Canvas mCanvas;         //
    private Path mPath;             // serve a definire il Percorso Path appunto tracciato, è quello che mi serve
    private Paint mBitmapPaint;     //
    public Context context;         //
    private ArrayList<Path> myPathUndo = new ArrayList<Path>();
    private ArrayList<Path> myPathRedo = new ArrayList<Path>();
    private Map<Path,Paint>pathColorMap = new HashMap<Path,Paint>();
    private Bitmap backBitmap;


    private boolean freeHand;
    private float mX,mY;
    private static final float TOUCH_TOLLERANCE = 0; // ricordarsi di cambiarlo old = 4
    private boolean strokePath;
    private boolean floodFill;
    private List<Pixel> listPcentr;

    private int[] pixels;
    private int sampleColor;
    private int[] sampleRBG;
    private int[] sampleMinMaxRGB;
    float stroke ;

    public ViewForDrawIn(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()){}
        this.context = context;
        init();
        Log.e(VFD_LOG, "Context context, AttributeSet attrs");
    }
    public void init(){
        mPath = new Path();
        mPaint = new Paint();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        stroke = mPaint.getStrokeWidth();
        Log.e(VFD_LOG, "Vdf inizialized");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap == null){
            Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(bitmap);
            pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
            bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,w,h);
        }else{
            mCanvas = new Canvas(mBitmap);
            pixels = new int[mBitmap.getHeight()*mBitmap.getWidth()];
            mBitmap.getPixels(pixels,0,mBitmap.getWidth(),0,0,w,h);
        }
    }
    public ColorMatrixColorFilter grayscale (){
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        return new ColorMatrixColorFilter(cm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        for (Path p : myPathUndo){
            Paint paint = pathColorMap.get(p);
            canvas.drawPath(p, paint);
            Log.e("ondraw Log", ""+myPathUndo.size());
        }
        canvas.drawPath(mPath, mPaint);
        Log.e(VFD_LOG, "draw");
    }
    private void touch_start(float x, float y){
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (listPcentr == null){
            listPcentr = new ArrayList<Pixel>();
            listPcentr.add(new Pixel((int)x,(int)y,mBitmap.getPixel((int)x,(int)y)));
        }
    }
    private void touch_move(float x,float y){
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);
        if (dx >= TOUCH_TOLLERANCE || dy >=TOUCH_TOLLERANCE){
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            if (listPcentr != null){
                listPcentr.add(new Pixel((int)x,(int)y,mBitmap.getPixel((int)x,(int)y)));
            }
        }
    }
    private void touch_up(float x, float y){
        mPath.lineTo(mX, mY);
        //mCanvas.drawPath(mPath, mPaint);
        if (listPcentr !=null){
            listPcentr.add(new Pixel((int)x,(int)y,mBitmap.getPixel((int)x,(int)y)));
        }
        if (freeHand) {
            pathColorMap.put(mPath, new Paint(mPaint));
            myPathUndo.add(mPath);
            //mCanvas.drawPath(mPath,mPaint); questo passaggio è necessario per disegnare sul canvas che poi verrà salvato anche se poi non farà parte dell Do Undo
            mPath = new Path();
        }
    }
    public void onUndoPath(){
        if (myPathUndo.size() > 0) {
            myPathRedo.add(myPathUndo.get(myPathUndo.size() - 1)); // da verificare
            myPathUndo.remove(myPathUndo.size() - 1);
            invalidate();
        }
    }
    public void onRedoPath(){
        if (myPathRedo.size()>0){
            myPathUndo.add(myPathRedo.get(myPathRedo.size()-1));
            myPathRedo.remove(myPathRedo.size()-1);
            invalidate();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) throws IllegalArgumentException{
        super.onTouchEvent(event);
        float y;
        y = event.getY();
        float x;
        x = event.getX();
        Log.e("on Touch choise", "freeHand :" + freeHand);
        Log.e("on Touch choise", "floodFill :" + floodFill);
        try {
            if (freeHand){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up(x, y);
                        //colorLog(x, y); // sarà da togliere alla fine del debug
                        invalidate();
                        break;
                }
            }
            if (floodFill){
                Bitmap image = null;
                int alpha = mPaint.getAlpha();
                mPaint.setAlpha(0);
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        touch_up(x,y);
                        colorLog(x, y); // sarà da togliere alla fine del debug
                        this.setDrawingCacheEnabled(true);
                        image = Bitmap.createBitmap(this.getDrawingCache());
                        sampleColor = mPaint.getColor();
                        break;
                }
                mPaint.setAlpha(alpha);
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return true;
    }

    public void colorLog(float x, float y){
        this.setDrawingCacheEnabled(true);
        Bitmap imgBtm = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        int pxl = imgBtm.getPixel((int) x, (int) y);
        int A,R,G,B;
        A = Color.alpha(pxl);
        R = Color.red(pxl);
        G = Color.green(pxl);
        B = Color.blue(pxl);
        int argb = Color.argb(A,R,G,B);
        Log.e(VFD_LOG,"Alpha: "+A+ " Red: "+R+" Green: "+G+" Blue: "+B);
    }
    public void findBord(int x, int y){ // usare un floodfill più leggero
        CannyEdgeDetector detector = new CannyEdgeDetector();
        detector.setLowThreshold(0.1f);
        detector.setHighThreshold(2.0f);
        detector.setSourceImage(mBitmap);
        detector.process();
        backBitmap = detector.getEdgesImage();
        mBitmap = backBitmap;
        invalidate();
    }

    public void floodFill(Bitmap bitmap, Pixel nestP){

    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        onSizeChanged(mBitmap.getWidth(),mBitmap.getHeight(),mBitmap.getWidth(),mBitmap.getHeight());
        Log.e(VFD_LOG,"on size changed recall");
    }

    public void setFreeHand(boolean freeHand) {
        this.freeHand = freeHand;
    }

    public void setStrokePath(boolean strokePath) {
        this.strokePath = strokePath;
    }

    public void setFloodFill(boolean floodFill) {
        this.floodFill = floodFill;
    }

    public Canvas getmCanvas() {
        return mCanvas;
    }

    public Paint getmBitmapPaint() {
        return mBitmapPaint;
    }
}

