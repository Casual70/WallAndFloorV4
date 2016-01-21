package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.Service.CannyEdgeDetector;
import com.filippowallandfloorv4.wallandfloorv4.Service.PrepareImage;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;


public class ViewForDrawIn extends View {

    private static final String VFD_LOG = "ViewForDrawIn_Debug";
    private static final float TOUCH_TOLLERANCE = 0; // ricordarsi di cambiarlo old = 4
    public Paint mPaint;
    public Context context;         //
    float stroke ;
    private Bitmap mBitmap;         //
    private Canvas mCanvas;         //
    private Canvas backCanvas;
    private Paint backPaint;
    private Path mPath;             // serve a definire il Percorso Path appunto tracciato, è quello che mi serve
    private Paint mBitmapPaint;     //
    private ArrayList<Path> myPathUndo = new ArrayList<Path>();
    private ArrayList<Path> myPathRedo = new ArrayList<Path>();
    private Path floodFillPath;
    private Map<Path,Paint>pathColorMap = new HashMap<Path,Paint>();
    private Bitmap backBitmap;
    private LinkedList<Bitmap> undoRedo;
    private LinkedList<Pixel>postElaboration;
    private LinkedList<Pixel>postFill;
    private boolean freeHand;
    private float mX,mY;
    private boolean floodFill;
    private List<Pixel> listPcentr;
    private WafImage wafImage;

    public ViewForDrawIn(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()){}
        this.context = App.getAppIstance().getContext();
        init();
    }
    public void init(){
        mPath = new Path();
        floodFillPath = new Path();
        mPaint = new Paint();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        stroke = mPaint.getStrokeWidth();
        Log.e(VFD_LOG, "Vdf inizialized");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mBitmap == null){
            Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(bitmap);
            Log.e(VFD_LOG, "Bitmap Null");
        }else{
            mCanvas = new Canvas(mBitmap);
            Log.e(VFD_LOG,"bimtap original W: "+ mBitmap.getWidth() + "bitmap original H: "+mBitmap.getHeight());
            Log.e(VFD_LOG,"Canvass original W: "+ mCanvas.getWidth() + "Canvass original H: "+mCanvas.getHeight());
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(VFD_LOG, "on Measure");
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
            Log.e("ondraw Log", ""+ myPathUndo.size());
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
                if (backBitmap != null){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            touch_up(x, y);
                            colorLog(x, y);
                            float stroke = mPaint.getStrokeWidth();
                            mPaint.setStrokeWidth(1.0f);
                            mPath.reset();
                            floodFill(backBitmap, new Pixel((int) x, (int) y, backBitmap.getPixel((int) x, (int) y)));
                            pathColorMap.put(floodFillPath, new Paint(mPaint));
                            myPathUndo.add(floodFillPath);
                            floodFillPath = new Path();
                            mPaint.setStrokeWidth(stroke);
                            break;
                    }
                }
            }
            if (!floodFill && !freeHand){
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        colorLog(x,y);
                }
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
        Log.e(VFD_LOG, "Alpha: " + A + " Red: " + R + " Green: " + G + " Blue: " + B);
    }
    public void findBord(){
        PrepareImage prepareImage = new PrepareImage(mBitmap,this);
        prepareImage.execute();

    }
    private void UndoRedoFull(Bitmap ActBitmap){
        if (undoRedo == null){
            undoRedo = new LinkedList<Bitmap>();
        }
        undoRedo.add(mBitmap);
    }

    public void floodFill(Bitmap bitmap, Pixel nestP){
        postElaboration = new LinkedList<>();
        postFill = new LinkedList<>();
        long start = System.currentTimeMillis();
        int x1 = nestP.x;
        while (x1< bitmap.getWidth()-1 && bitmap.getPixel(x1, nestP.y) == Color.BLACK) { //check get width 11 è sbagliato
            fillY(bitmap,new Pixel(x1,nestP.y,bitmap.getPixel(x1, nestP.y)));
            x1++;
        }
        int x2 = nestP.x-1;
        while (x2 > 1 && bitmap.getPixel(x2,nestP.y) == Color.BLACK){ //check get width 11 è sbagliato
            fillY(bitmap, new Pixel(x2, nestP.y, bitmap.getPixel(x2, nestP.y)));
            x2--;
        }
        for (Pixel post:postElaboration){
            bitmap.setPixel(post.x,post.y,Color.RED);
        }
        LinkedList<LinkedList> listOfBorder = postFill(bitmap);
        if (!listOfBorder.isEmpty()){
            for (LinkedList list : listOfBorder){
                if (list.size() >= 10){
                Pixel newNest = (Pixel) list.get(list.size()/2);
                floodFill(bitmap,newNest);
                }
            }
        }

        long finish = (System.currentTimeMillis()-start);
        Log.e(VFD_LOG,"tempo impiegato: "+finish);

        //mBitmap = bitmap;

        invalidate();
    }
    private LinkedList<LinkedList> postFill(Bitmap bitmap){ // todo rivedere questo metodo e velocizzarlo
        for (Pixel post:postElaboration){
            if (bitmap.getPixel(post.x-1,post.y) == Color.BLACK && bitmap.getPixel(post.x+2,post.y) == Color.RED){
                postFill.add(new Pixel(post.x-1,post.y,Color.BLACK));
            }
            if (bitmap.getPixel(post.x+1,post.y) == Color.BLACK && bitmap.getPixel(post.x-2,post.y) == Color.RED){
                postFill.add(new Pixel(post.x+1,post.y,Color.BLACK));
            }
        }// this is ok
        LinkedList<LinkedList>listOfAllBorder = new LinkedList<>();
        LinkedList<Pixel>singleBorderLine = new LinkedList<>();
        ListIterator<Pixel> iterator = postFill.listIterator();
        int previusY = 0;
        while (iterator.hasNext()){
            Pixel p = iterator.next();
            Log.e(VFD_LOG, "Y: "+p.y);
            if (previusY == 0 || (previusY+1 != p.y && previusY-1 != p.y)){
                Log.e(VFD_LOG, "previus is null or different");
                if (!singleBorderLine.isEmpty()){
                    LinkedList<Pixel> temp = new LinkedList<>();
                    temp.addAll(singleBorderLine);
                    listOfAllBorder.add(temp);
                    Log.e(VFD_LOG, "creta nuova lista size: "+temp.size());
                }
                singleBorderLine.clear();
                singleBorderLine.add(p);
                previusY = p.y;
            }else {
                Log.e(VFD_LOG," Y in sequenza");
                if (previusY+1 == p.y || previusY-1 == p.y){
                    singleBorderLine.add(p);
                }
            }
            Log.e(VFD_LOG,"previus; "+previusY + " pixel: "+p.y);
            previusY = p.y;
        }
        if (!singleBorderLine.isEmpty()){
            LinkedList<Pixel> temp = new LinkedList<>();
            temp.addAll(singleBorderLine);
            listOfAllBorder.add(temp);
            Log.e(VFD_LOG, "creta nuova lista finale size: "+temp.size());
        }

        Log.e(VFD_LOG, "border n: " + listOfAllBorder.size());
        int[] color = {Color.RED,Color.GREEN,Color.BLUE,Color.WHITE};
        for (LinkedList p : listOfAllBorder){
            Random random = new Random();
            int x = random.nextInt(3);
            for (Pixel pix : (LinkedList<Pixel>)p){
                bitmap.setPixel(pix.x,pix.y,Color.BLACK);
            }
        }
        return listOfAllBorder;
    }

    private boolean fillY(Bitmap bitmap, Pixel nestP){
        int y1 = nestP.y;
        LinkedList<Pixel> pixelsY1 = new LinkedList<>();
        while (y1 < bitmap.getHeight() - 1 && bitmap.getPixel(nestP.x, y1) == Color.BLACK) {
            Pixel p = new Pixel(nestP.x, y1, bitmap.getPixel(nestP.x, y1));
            p.setVisited(true);
            pixelsY1.add(p);
            y1++;
        }
        int y2 = nestP.y;
        LinkedList<Pixel> pixelsY2 = new LinkedList<>();
        while (y2 > 1 && bitmap.getPixel(nestP.x, y2) == Color.BLACK) {
            Pixel p = new Pixel(nestP.x, y2, bitmap.getPixel(nestP.x, y2));
            p.setVisited(true);
            pixelsY2.add(p);
            y2--;
        }
        Path path = new Path();
        path.moveTo(pixelsY2.getLast().x, pixelsY2.getLast().y);
        path.lineTo(pixelsY1.getLast().x, pixelsY1.getLast().y);
        //mCanvas.drawPath(path, mPaint);
        floodFillPath.addPath(path);

        postElaboration.addAll(pixelsY1);
        postElaboration.addAll(pixelsY2);
        Log.e(VFD_LOG,"postElaboration size: "+postElaboration.size());
        return true;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        //onSizeChanged(mBitmap.getWidth(),mBitmap.getHeight(),mBitmap.getWidth(),mBitmap.getHeight());
        //Log.e(VFD_LOG,"on size changed recall");
    }

    public void setFreeHand(boolean freeHand) {
        this.freeHand = freeHand;
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

    public void setBackBitmap(Bitmap backBitmap) {
        this.backBitmap = backBitmap;
    }

    public Canvas getBackCanvas() {
        return backCanvas;
    }

    public void setBackCanvas(Canvas backCanvas) {
        this.backCanvas = backCanvas;
    }

    public Paint getBackPaint() {
        return backPaint;
    }

    public void setBackPaint(Paint backPaint) {
        this.backPaint = backPaint;
    }

    public WafImage getWafImage() {
        return wafImage;
    }

    public void setWafImage(WafImage wafImage) {
        this.wafImage = wafImage;
    }

    public void finallyDraw() {
        for (Path p : myPathUndo) {
            Paint paint = pathColorMap.get(p);
            mCanvas.drawPath(p, paint);
        }
        mCanvas.drawPath(mPath,mPaint);
    }
}

