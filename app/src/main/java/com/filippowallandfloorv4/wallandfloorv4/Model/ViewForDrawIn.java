package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
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
    private ArrayList<Point> listPcentr = null;
    private ArrayList<Point> listPup = null;
    private ArrayList<Point> listPdw = null;
    private ArrayList<Path> myPathUndo = new ArrayList<Path>();
    private ArrayList<Path> myPathRedo = new ArrayList<Path>();
    private Map<Path,Paint>pathColorMap = new HashMap<Path,Paint>();


    private boolean freeHand;
    private float mX,mY;
    private static final float TOUCH_TOLLERANCE = 0; // ricordarsi di cambiarlo old = 4
    private boolean strokePath;
    private boolean oneLine;
    private Point fp;
    private Point lp;

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
        }else{
            mCanvas = new Canvas(mBitmap);
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
            canvas.drawPath(p,paint);
        }
        canvas.drawPath(mPath, mPaint);
        Log.e(VFD_LOG, "draw");
    }
    private void touch_start(float x,float y){
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (listPcentr ==null){
            listPcentr = new ArrayList<Point>();
            fp = new Point((int)x,(int)y);
        }
    }
    private void touch_move(float x,float y){
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);
        if (dx >= TOUCH_TOLLERANCE || dy >=TOUCH_TOLLERANCE){
            mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
            mX = x;
            mY = y;
            if (listPcentr != null){
                Point p = new Point((int)x,(int)y);
                colorLog(p.x,p.y);
                Log.e(VFD_LOG, "" + p);
                listPcentr.add(p);
            }
        }
    }
    private void touch_up(float x, float y){
        mPath.lineTo(mX, mY);
        //mCanvas.drawPath(mPath, mPaint);
        if (listPcentr !=null){
            lp = new Point((int)x,(int)y);
        }
        if (freeHand){ // funziona per il freehand implementarlo anche per il oneline facendo si che tutta la maschera sia un singolo path
            pathColorMap.put(mPath, new Paint(mPaint));
            myPathUndo.add(mPath);
        }

        mPath = new Path();
    }
    public void onUndoPath(){
        if (myPathUndo.size()>0){
            myPathRedo.add(myPathUndo.get(myPathUndo.size()-1)); // da verificare
            myPathUndo.remove(myPathUndo.size()-1);
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

    private void floodFillOneLine(Bitmap image,ArrayList<Point>listPathPoint){
        mPath.reset();
        mPath.moveTo(listPathPoint.get(0).x, listPathPoint.get(0).y);
        Log.e(VFD_LOG, "lunghezza lista punti: " + listPathPoint.size());
        for (Point p : listPathPoint){ // funzione riempimento horizzont
            Point original = new Point(p.x,p.y);
            boolean findXDx = false;
            boolean findXSx = false;
            while (!findXDx){
                if (!findIntMaxMin(p,sampleMinMaxRGB,image) || p.x<1){
                    findXDx = true;
                    Log.e(VFD_LOG, "punto: "+ p);
                }else{
                    mPath.lineTo(p.x,p.y);
                    p.x--;
                }
            }
            while (!findXSx){
                if (!findIntMaxMin(original,sampleMinMaxRGB,image) || original.x >= image.getWidth()-1){
                    findXSx = true;
                }else{
                    mPath.lineTo(original.x,original.y);
                    original.x++;
                }
            }
        }
        mCanvas.drawPath(mPath, mPaint);
    }

    private void extendLineUp(Bitmap image){
        listPup = new ArrayList<>();
        listPup.add(new Point(fp.x, fp.y));
        Point mP = new Point(fp.x,fp.y-1);
        Log.e(VFD_LOG, fp + " " + mP);
        boolean find = false;
        while(!find){
            if (!findIntMaxMin(mP,sampleMinMaxRGB,image) || mP.y<1){
                Log.e(VFD_LOG, "trovata differenza " + mP);
                colorLog(mP.x, mP.y);
                find = true;
            }else{
                listPup.add(new Point(mP.x,mP.y));
                mP.set(mP.x, mP.y - 1);
                Log.e(VFD_LOG, "aggiunto punto "+mP);
            }
        }
    }

    private void extendLineDown(Bitmap image){
        listPdw = new ArrayList<>();
        listPdw.add(new Point(lp.x, lp.y));
        Point mP = new Point(lp.x,lp.y+1);
        boolean find = false;
        while(!find){
            if (!findIntMaxMin(mP,sampleMinMaxRGB,image)||mP.y>image.getHeight()){
                find = true;
            }else{
                listPdw.add(new Point(mP.x, mP.y));
                mP.set(mP.x, mP.y + 1);
            }
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
        Log.e("on Touch choise", "oneLine :" + oneLine);
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
            if (oneLine){
                Bitmap image = null;
                int alpha = mPaint.getAlpha();
                mPaint.setAlpha(0);
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up(x,y);
                        colorLog(x, y); // sarà da togliere alla fine del debug
                        this.setDrawingCacheEnabled(true);
                        image = Bitmap.createBitmap(this.getDrawingCache());
                        sampleColor = mPaint.getColor();
                        invalidate();
                        break;
                }
                mPaint.setAlpha(alpha);
                if (listPcentr !=null && image !=null){
                    sampleMinMaxRGB = tolleranceMaxMinColor(listPcentr,image);
                    extendLineUp(image);
                    extendLineDown(image);
                    if (listPup.size()!=0){
                        Log.e(VFD_LOG, "second array size " + listPup.size());
                        listPcentr.addAll(listPup);
                        if (listPdw.size()!=0){
                            listPcentr.addAll(listPdw);
                        }
                        floodFillOneLine(image, listPcentr);
                    }
                    listPup = null;
                    listPdw = null;
                    listPcentr =null;
                }
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return true;
    }

    private int[] tolleranceMaxMinColor(ArrayList<Point> points, Bitmap image){
        List<Integer> redList = new ArrayList<>();
        List<Integer> greeList = new ArrayList<>();
        List<Integer>blueList = new ArrayList<>();

        for (Point p : points){
            int [] analisRGB ={Color.red(image.getPixel(p.x,p.y)),
                               Color.green(image.getPixel(p.x, p.y)),
                               Color.blue(image.getPixel(p.x,p.y))};
            redList.add(analisRGB[0]);
            greeList.add(analisRGB[1]);
            blueList.add(analisRGB[2]);
        }
        int redMax = Collections.max(redList);
        int redMin = Collections.min(redList);
        int greeMax = Collections.max(greeList);
        int greeMin = Collections.min(greeList);
        int blueMax = Collections.max(blueList);
        int blueMin = Collections.min(blueList);

        Log.e("AVERANGECOLOR","Red max: "+redMax+" Red min: "+redMin);
        Log.e("AVERANGECOLOR","Green max: "+greeMax+" Green min: "+greeMin);
        Log.e("AVERANGECOLOR","Blue max: "+blueMax+" Blue min: "+blueMin);

        //provare ad usare la media dei colori ottenuti oppure impostare i min e max
        return new int[] {redMax,redMin,greeMax,greeMin,blueMax,blueMin};
    }

    private boolean findIntMaxMin(Point pixel, int[] maxMinToll,Bitmap image){
        boolean find = false;
        int tollerace = 10;
        if (pixel.x>=1 && pixel.x<=image.getWidth()-1 && pixel.y>=1 && pixel.y<=image.getHeight()-1){
            int [] analisRGB ={Color.red(image.getPixel(pixel.x,pixel.y)),
                               Color.green(image.getPixel(pixel.x, pixel.y)),
                               Color.blue(image.getPixel(pixel.x,pixel.y))};

            if (    analisRGB[0]<=maxMinToll[0]+tollerace && analisRGB[0] >= maxMinToll[1]-tollerace&&
                    analisRGB[1]<=maxMinToll[2]+tollerace && analisRGB[1] >= maxMinToll[3]-tollerace&&
                    analisRGB[2]<=maxMinToll[4]+tollerace && analisRGB[2] >= maxMinToll[5]-tollerace) {
                find = true;
            }
        }
        return find;
    }

    public void colorLog(float x, float y){
        this.setDrawingCacheEnabled(true);
        Bitmap imgBtm = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        int pxl = imgBtm.getPixel((int) x, (int) y);
        int R,G,B;
        R = Color.red(pxl);
        G = Color.green(pxl);
        B = Color.blue(pxl);
        Log.e(VFD_LOG," Red: "+R+" Green: "+G+" Blue: "+B);
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

    public void setOneLine(boolean oneLine) {
        this.oneLine = oneLine;
    }

    public Canvas getmCanvas() {
        return mCanvas;
    }

    public Paint getmBitmapPaint() {
        return mBitmapPaint;
    }
}

