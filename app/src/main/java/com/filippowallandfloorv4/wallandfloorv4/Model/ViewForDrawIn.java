package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.filippowallandfloorv4.wallandfloorv4.Activity.EditorActivity;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Fragment.EditorFragment;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.Service.CannyImage;
import com.filippowallandfloorv4.wallandfloorv4.Service.HarrysCorner;
import com.filippowallandfloorv4.wallandfloorv4.Service.HougeImage;
import com.google.common.base.Function;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Primitives;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


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
    private ArrayList<Path> myPathUndo = new ArrayList<>();
    private ArrayList<Path> myPathRedo = new ArrayList<>();
    private ArrayList<LinkedList> myPathUndoBack = new ArrayList<>();
    private ArrayList<LinkedList> myPathRedoBack = new ArrayList<>();
    private Path floodFillPath;
    private Map<Path,Paint>pathColorMap = new HashMap<>();
    private Map<Path,LinkedList>pathBackMap = new HashMap<>();
    private Bitmap backBitmap;
    private Mat cannyMat;
    private LinkedList<Mypixel>postElaboration;
    private LinkedList<Mypixel>postFill;
    private LinkedList<Mypixel>visitedBackPixel;
    private boolean freeHand;
    private List<Point>prospectPoitList;
    private List<Point>sourcePoitList;
    private float mX,mY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int canvasBoundX;
    private int canvasBoundY;
    private float scalePointX;
    private float scalePointY;
    private int mActivePointerId = -1;
    private boolean floodFill;
    private List<Mypixel> listPcentr;
    private WafImage wafImage;
    private ScaleGestureDetector SGD;
    private float mScaleFactor = 1.0f;
    private EditorFragment editorFragment;
    private Bitmap mTextureBitmapVFD;

    public ViewForDrawIn(Context context, AttributeSet attrs) {
        super(context, attrs);
        isInEditMode();
        this.context = App.getAppIstance().getContext();
        init();
    }
    public void init(){
        mPath = new Path();
        floodFillPath = new Path();
        mPaint = new Paint();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        stroke = mPaint.getStrokeWidth();
        SGD = new ScaleGestureDetector(context,new ScaleListener());
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, scalePointX, scalePointY);
        Rect canvasBound = canvas.getClipBounds();
        canvasBoundX = canvasBound.left;
        canvasBoundY = canvasBound.top;
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        for (Path p : myPathUndo){
            Paint paint = pathColorMap.get(p);
            canvas.drawPath(p, paint);
            Log.e("ondraw Log", ""+ myPathUndo.size());
        }
        /**if (prospectPoitList != null){
            Paint paint = new Paint(); // vedere se è necessaria farle come varibili globali
            paint.setColor(Color.RED);
            Path pointPath = new Path();
            for (Point p : prospectPoitList){
                pointPath.addCircle((float)p.x,(float)p.y,15, Path.Direction.CCW);
                canvas.drawPath(pointPath,paint);
            }
        }*/
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        Log.e(VFD_LOG, "draw");
    }

    private void touch_start(float x, float y){
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if (listPcentr == null){
            listPcentr = new ArrayList<>();
            listPcentr.add(new Mypixel((int)x,(int)y,mBitmap.getPixel((int)x,(int)y)));
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
                listPcentr.add(new Mypixel((int)x,(int)y,mBitmap.getPixel((int)x,(int)y)));
            }
        }
    }
    private void touch_up(float x, float y){
        mPath.lineTo(mX, mY);
        if (listPcentr !=null){
            listPcentr.add(new Mypixel((int) x, (int) y, mBitmap.getPixel((int) x, (int) y)));
        }
        if (freeHand) {
            pathColorMap.put(mPath, new Paint(mPaint));
            myPathUndo.add(mPath);
            //mCanvas.drawPath(mPath,mPaint); questo passaggio è necessario per disegnare sul canvas che poi verrà salvato anche se poi non farà parte dell Do Undo
            mPath = new Path();
        }
    }
    public List<Point> load4Point(float x, float y , Bitmap mTextureBitmapVFD){

        ImageViewPointer point = new ImageViewPointer(context,x,y);
        if (prospectPoitList == null){
            prospectPoitList = new ArrayList<>();
        }
        if (prospectPoitList.size()<4){
            prospectPoitList.add(new Point(point.getX(), point.getY()));
            point.setImageDrawable(getResources().getDrawable(R.drawable.destra));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(15,15);
            params.leftMargin = (int)point.getX();
            params.topMargin = (int)point.getY();
            editorFragment.relativeLayout_editor.addView(point, params);

        }

        /**Load4point()
        - achiviare le ImageVPointer per poi rimuoverle alla fine (magari al posto del prospectPoitList)
                - implementare il Drag sulle image view dove ogni volta che vengono spostate si deve ridisegnare le linee di selezione
        - per le linee di selezione usare un Path a parte da eliminare alla fine con le image view
                - implementare un tasto o un gesto di conferma alla fine del posizionamento
                - nel drag aggiungere anche il metodo per prospettivizzare l'immagine del bitmap che
                -le linee di selezione e i 4 punti devono assere presenti da subito con il bitmap disegmato il fill piatto
                - al drag si avrà la prospettiva secondo gli attuali parametri*/

        return prospectPoitList;
    }
    public void onUndoPath(){
        if (myPathUndo.size() > 0) {
            myPathRedo.add(myPathUndo.get(myPathUndo.size() - 1));
            myPathUndo.remove(myPathUndo.size() - 1);
            invalidate();
        }
        if (myPathUndoBack.size() > 0){
            LinkedList<Mypixel> list = myPathUndoBack.get(myPathUndoBack.size()-1);
            for (Mypixel p : list){
                backBitmap.setPixel(p.x,p.y,Color.BLACK);
            }
            myPathRedoBack.add(list);
            myPathUndoBack.remove(list);
        }
    }
    public void onRedoPath(){
        if (myPathRedo.size()>0){
            myPathUndo.add(myPathRedo.get(myPathRedo.size()-1));
            myPathRedo.remove(myPathRedo.size()-1);
            invalidate();
        }
        if (myPathRedoBack.size()>0){
            LinkedList<Mypixel> list = myPathRedoBack.get(myPathRedoBack.size()-1);
            for (Mypixel p : list){
                backBitmap.setPixel(p.x,p.y,Color.RED);
            }
            myPathUndoBack.add(list);
            myPathRedoBack.remove(list);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) throws IllegalArgumentException{
        super.onTouchEvent(event);
        mY = event.getY()/mScaleFactor+canvasBoundY;
        mX = event.getX()/mScaleFactor+canvasBoundX;
        Log.e("on Touch choise", "freeHand :" + freeHand);
        Log.e("on Touch choise", "floodFill :" + floodFill);
        try {
            if (event.getPointerCount()>1){   // picht zoom
                Log.e("Puntatori attivi", "Puntatori attivi : " + event.getPointerCount());
                SGD.onTouchEvent(event);
                freeHand = false;
                floodFill = false;
                editorFragment.freeHandToggleB.setChecked(freeHand);
                editorFragment.oneLineToggleB.setChecked(floodFill);
            }else{
                Log.e("Puntatori attivi", "Puntatori attivi : " + event.getPointerCount());
                if (freeHand){
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touch_start(mX, mY);
                            invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touch_move(mX, mY);
                            invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            touch_up(mX, mY);
                            //colorLog(x, y); // sarà da togliere alla fine del debug
                            invalidate();
                            break;
                    }
                }
                if (floodFill){
                    if (backBitmap != null ){
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_UP:
                                touch_up(mX, mY);
                                colorLog(mX, mY);
                                float stroke = mPaint.getStrokeWidth();
                                mPaint.setStrokeWidth(1.0f);
                                mPath.reset();
                                if (mPaint.getShader() != null){                                            //nel caso si stiano stabiledondo i punti di prospetto aprire
                                    if (prospectPoitList == null || prospectPoitList.size()<4){
                                        mTextureBitmapVFD = editorFragment.mTextureBitmap;
                                        prospectPoitList = load4Point(mX,mY, mTextureBitmapVFD);
                                        String listPoint = "";
                                        /**for(int index = 0;index<prospectPoitList.size();index++){
                                            listPoint += ("Punto "+index+" X: "+prospectPoitList.get(index).x + " Y: "+prospectPoitList.get(index).y+"\n");
                                        }
                                        Log.e("ProspectListSize"," "+prospectPoitList.size());
                                        Toast.makeText(context,listPoint,Toast.LENGTH_SHORT).show();*/
                                        return true;
                                    }

                                    if (prospectPoitList.size() == 4){
                                        Bitmap prepare = prepareTexture();
                                        Bitmap newTexture = prospectTexture(sourcePoitList,prospectPoitList, prepare);
                                        if (newTexture == null){
                                            Log.e(VIEW_LOG_TAG,"texture null");
                                            return true;
                                        }
                                        BitmapShader shader = new BitmapShader(newTexture, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                                        mPaint.setShader(shader);
                                        Toast.makeText(context,"i 4 punti di prospettivizzazione sono stati caricati",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context,"Shader == Null",Toast.LENGTH_SHORT).show();
                                }
                                visitedBackPixel = new LinkedList<>();
                                floodFill(backBitmap, new Mypixel((int) mX, (int) mY, backBitmap.getPixel((int) mX, (int) mY)));
                                pathColorMap.put(floodFillPath, new Paint(mPaint));
                                myPathUndo.add(floodFillPath);
                                Log.e("Visited pixel", "Visited Pixel tot : " + visitedBackPixel.size());
                                myPathUndoBack.add(visitedBackPixel);
                                //findCorner(visitedBackPixel);
                                visitedBackPixel = new LinkedList<>();
                                floodFillPath = new Path();
                                mPaint.setStrokeWidth(stroke);
                                break;
                        }
                    }
                }
                if (!floodFill && !freeHand){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_UP:
                            colorLog(mX,mY);
                    }
                }
            }

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return true;
    }
    private LinkedList<Mypixel> findCorner (LinkedList<Mypixel> visitedBackPixel){

        LinkedList<Mypixel> allPointOrderXmintoMax = new LinkedList<>(visitedBackPixel);
        LinkedList<Mypixel> allPointOrderYmintoMax = new LinkedList<>(visitedBackPixel);

        LinkedList<Mypixel>corner = new LinkedList<Mypixel>();
        //1:alto sinistra blu
        //2:basso sinistra rosso
        //3:basso destra green
        //4:alto destra giallo
        Ordering<Mypixel> orderingMinX = new Ordering<Mypixel>() {
            @Override
            public int compare(Mypixel left, Mypixel right) {
                return Ints.compare(left.x,right.x);
            }
        };
        Ordering<Mypixel>orderingMinY = new Ordering<Mypixel>() {
            @Override
            public int compare(Mypixel left, Mypixel right) {
                return Ints.compare(left.y,right.y);
            }
        };

        Collections.sort(allPointOrderXmintoMax,orderingMinX);
        /**for (Mypixel p: allPointOrderXmintoMax){
            Log.e("List of MyPixe", " " +p.x+"  "+p.y );
        }*/
        LinkedList<LinkedList<Mypixel>> allPointForListofMyPixelx = new LinkedList<>();
        LinkedList<Mypixel>myPixelforX = new LinkedList<>();
        ListIterator<Mypixel>iterator = allPointOrderXmintoMax.listIterator();
        int previusX = 0;
        while (iterator.hasNext()){
            Mypixel p = iterator.next();
            if (previusX == 0){
                previusX = p.x;
            }
            if (p.x == previusX){
                myPixelforX.add(p);
            }else {
                LinkedList<Mypixel>temp = new LinkedList<>();
                temp.addAll(myPixelforX);
                allPointForListofMyPixelx.add(temp);
                myPixelforX.clear();
                previusX = p.x;
                myPixelforX.add(p);
            }
        }
        Log.e("size", " "+allPointForListofMyPixelx.size());
        //Log.e("size of index 10"," "+allPointForListofMyPixelx.get(10).size());

        LinkedList<Mypixel>listMinorYforEveryX = new LinkedList<>();
        LinkedList<Mypixel>listMaxYforEveryX = new LinkedList<>();
        for (LinkedList<Mypixel> listEveryX : allPointForListofMyPixelx) {
            Collections.sort(listEveryX, orderingMinY);
            listMinorYforEveryX.add(listEveryX.getFirst());
            listMaxYforEveryX.add(listEveryX.getLast());
        }
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        for (Mypixel p : listMinorYforEveryX){
            mCanvas.drawCircle(p.x, p.y, 5, paint);
        }
        paint.setColor(Color.GREEN);
        for (Mypixel p : listMaxYforEveryX){
            mCanvas.drawCircle(p.x,p.y,5,paint);
        }




        /** prima ipotesi
        Collections.sort(allPointOrderXmintoMax, orderingMinX);
        LinkedList<Mypixel>listOfYforMinX = new LinkedList<>();
        for (Mypixel p : allPointOrderXmintoMax){
            if (p.x == allPointOrderXmintoMax.getFirst().x){
                listOfYforMinX.add(p);
            }
        }
        Mypixel minXforMinY = Collections.min(listOfYforMinX,orderingMinY); // primo punto //alto sinistra // blu
        Log.e("final", "" + minXforMinY.x + " " + minXforMinY.y);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        mCanvas.drawCircle((float) minXforMinY.x, (float) minXforMinY.y, 5, paint);
        corner.add(minXforMinY);

        Collections.sort(allPointOrderYmintoMax, orderingMinY);
        LinkedList<Mypixel>listOfXforMinY = new LinkedList<>();
        for (Mypixel p : allPointOrderYmintoMax){
            if (p.y == allPointOrderYmintoMax.getLast().y){
                listOfXforMinY.add(p);
            }
        }
        Mypixel minXforMaxY = Collections.min(listOfXforMinY,orderingMinX); //secondo punto // basso sinistra // red
        paint.setColor(Color.RED);
        mCanvas.drawCircle((float) minXforMaxY.x, (float) minXforMaxY.y, 5, paint);
        corner.add(minXforMaxY);

        Collections.sort(allPointOrderXmintoMax, orderingMinX);
        LinkedList<Mypixel>listOfYforMaxX = new LinkedList<>();
        for (Mypixel p : allPointOrderXmintoMax){
            if (p.x == allPointOrderXmintoMax.getLast().x){
                listOfYforMaxX.add(p);
            }
        }
        Mypixel maxXforMaxY = Collections.max(listOfYforMaxX,orderingMinY); //terzo punto // basso destra // green
        paint.setColor(Color.GREEN);
        mCanvas.drawCircle((float) maxXforMaxY.x, (float) maxXforMaxY.y, 5, paint);
        corner.add(maxXforMaxY);

        Collections.sort(allPointOrderYmintoMax, orderingMinY);
        LinkedList<Mypixel>listofXforMaxY = new LinkedList<>();
        for (Mypixel p : allPointOrderYmintoMax){
            if (p.y == allPointOrderYmintoMax.getFirst().y){
                listofXforMaxY.add(p);
            }
        }
        Mypixel maxXforMinY = Collections.max(listofXforMaxY,orderingMinY); //quarto punto // alto destra // giallo
        paint.setColor(Color.YELLOW);
        mCanvas.drawCircle((float) maxXforMinY.x, (float) maxXforMinY.y, 5, paint);
        corner.add(maxXforMinY);
        */
        return corner;
    }


    public Bitmap prepareTexture(){
        Mat mBitmapMat = new Mat();
        Utils.bitmapToMat(mBitmap, mBitmapMat);
        Bitmap fullBitmapTexture = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(mTextureBitmapVFD, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Canvas can = new Canvas(fullBitmapTexture);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAlpha(255);
        can.drawPaint(paint);
        Point one,two,tree,four;
        one = new Point((mBitmap.getWidth()/2)-(mTextureBitmapVFD.getWidth()/2),(mBitmap.getHeight()/2)-(mTextureBitmapVFD.getHeight()/2));
        two = new Point(one.x,one.y+mTextureBitmapVFD.getHeight());
        tree = new Point(one.x+mTextureBitmapVFD.getWidth(),one.y+mTextureBitmapVFD.getHeight());
        four = new Point(one.x + mTextureBitmapVFD.getWidth(),one.y);
        sourcePoitList = new ArrayList<Point>();
        sourcePoitList.add(one);sourcePoitList.add(two);sourcePoitList.add(tree);sourcePoitList.add(four);
        Log.e("PointSource One", " One x: " + one.x + " One y: " + one.y);
        Log.e("PointSource Two", " Two x: " + two.x + " Two y: " + two.y);
        Log.e("PointSource Tree", " Tree x: " + tree.x + " Tree y: " + tree.y);
        Log.e("PointSource Four"," Four x: "+four.x + " Four y: "+four.y);
        return fullBitmapTexture;
    }

    private Bitmap prospectTexture(List<Point>sourcePointList, List<Point>prospectPointList ,Bitmap paddingTexture){
        Mat textureImage = new Mat();
        Utils.bitmapToMat(paddingTexture, textureImage);
        Mat outputimage = new Mat(textureImage.rows(),textureImage.cols(),textureImage.type());

        Mat startM = Converters.vector_Point2f_to_Mat(Arrays.asList(
                new Point(sourcePointList.get(0).x,sourcePointList.get(0).y),
                new Point(sourcePointList.get(1).x,sourcePointList.get(1).y),
                new Point(sourcePointList.get(2).x,sourcePointList.get(2).y),
                new Point(sourcePointList.get(3).x,sourcePointList.get(3).y)));

        Mat endM = Converters.vector_Point2f_to_Mat(prospectPointList);

        //OpenCV Error: Assertion failed (src.checkVector(2, CV_32F) == 4 && dst.checkVector(2, CV_32F) == 4)
        Log.e("Assertion", "valori end: " + endM.checkVector(2, CvType.CV_32F));
        Log.e("Assertion", "valori start: " + startM.checkVector(2, CvType.CV_32F));


        Mat trasform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(textureImage, outputimage, trasform, textureImage.size(), Imgproc.INTER_CUBIC);
        Bitmap outputBitmap = Bitmap.createBitmap(outputimage.cols(),outputimage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputimage, outputBitmap);
        invalidate();
        Log.e("dimens", "bitmap Width :" + mBitmap.getWidth() + " bimat Height: " + mBitmap.getHeight());
        Log.e("dimens","texture Width :"+paddingTexture.getWidth() + " texture Height: "+ paddingTexture.getHeight());
        return outputBitmap;
    }

    private double scalePointProspX(int WTextureBitmap,int WBitmap,double point){
        double rapporto = WTextureBitmap/WBitmap;
        return point*rapporto;
    }
    private double scalePointProspY(int HTextureBitmap,int HBitmap,double point){
        double rapporto = HTextureBitmap/HBitmap;
        return point*rapporto;
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
        Log.e(VFD_LOG, "X: " + x + " Y: " + y);
    }
    public void CannyBord(){
        CannyImage cannyImage = new CannyImage(mBitmap,this);
        cannyImage.execute();
    }
    public void HougeBord(){
        if (cannyMat == null){
            return;
        }
        HougeImage hougeImage = new HougeImage(cannyMat,this,mBitmap);
        hougeImage.execute();
    }
    public void CornerDetector(){
        if (cannyMat == null){
            return;
        }
        HarrysCorner corner = new HarrysCorner(cannyMat,this,mBitmap);
        corner.execute();
    }

    public void floodFill(Bitmap bitmap, Mypixel nestP){
        postElaboration = new LinkedList<>();
        postFill = new LinkedList<>();
        long start = System.currentTimeMillis();
        int x1 = nestP.x;
        while (x1< bitmap.getWidth()-1 && bitmap.getPixel(x1, nestP.y) == Color.BLACK) { //check get width 11 è sbagliato
            fillY(bitmap, new Mypixel(x1, nestP.y, bitmap.getPixel(x1, nestP.y)));
            x1++;
        }
        int x2 = nestP.x-1;
        while (x2 > 1 && bitmap.getPixel(x2,nestP.y) == Color.BLACK){ //check get width 11 è sbagliato
            fillY(bitmap, new Mypixel(x2, nestP.y, bitmap.getPixel(x2, nestP.y)));
            x2--;
        }
        for (Mypixel post:postElaboration){
            bitmap.setPixel(post.x,post.y,Color.RED);
            post.setVisited(true);
            visitedBackPixel.add(post);
        }
        LinkedList<LinkedList> listOfBorder = postFill(bitmap);
        if (!listOfBorder.isEmpty()){
            for (LinkedList list : listOfBorder){
                if (list.size() >= 10){
                Mypixel newNest = (Mypixel) list.get(list.size()/2);
                floodFill(bitmap,newNest);
                }
            }
        }
        long finish = (System.currentTimeMillis()-start);
        Log.e(VFD_LOG, "tempo impiegato: " + finish);

        //mBitmap = bitmap;

        invalidate();
    }
    private LinkedList<LinkedList> postFill(Bitmap bitmap){
        for (Mypixel post:postElaboration){
            if (post.x!=0 && bitmap.getPixel(post.x-1,post.y) == Color.BLACK && bitmap.getPixel(post.x+2,post.y) == Color.RED){
                postFill.add(new Mypixel(post.x-1,post.y,Color.BLACK));
            }else if (post.x <2){
                post.x = 2;
            }
            if (post.x != bitmap.getHeight() && bitmap.getPixel(post.x+1,post.y) == Color.BLACK && bitmap.getPixel(post.x-2,post.y) == Color.RED){
                postFill.add(new Mypixel(post.x+1,post.y,Color.BLACK));
            }
        }// this is ok
        LinkedList<LinkedList>listOfAllBorder = new LinkedList<>();
        LinkedList<Mypixel>singleBorderLine = new LinkedList<>();
        ListIterator<Mypixel> iterator = postFill.listIterator();
        int previusY = 0;
        while (iterator.hasNext()){
            Mypixel p = iterator.next();
            Log.e(VFD_LOG, "Y: "+p.y);
            if (previusY == 0 || (previusY+1 != p.y && previusY-1 != p.y)){
                Log.e(VFD_LOG, "previus is null or different");
                if (!singleBorderLine.isEmpty()){
                    LinkedList<Mypixel> temp = new LinkedList<>();
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
            LinkedList<Mypixel> temp = new LinkedList<>();
            temp.addAll(singleBorderLine);
            listOfAllBorder.add(temp);
            Log.e(VFD_LOG, "creta nuova lista finale size: "+temp.size());
        }

        Log.e(VFD_LOG, "border n: " + listOfAllBorder.size());
        /**int[] color = {Color.RED,Color.GREEN,Color.BLUE,Color.WHITE};
        for (LinkedList p : listOfAllBorder){
            Random random = new Random();
            int x = random.nextInt(3);
            for (Mypixel pix : (LinkedList<Mypixel>)p){
                bitmap.setPixel(pix.x,pix.y,Color.BLACK);
            }
        }*/
        return listOfAllBorder;
    }

    private boolean fillY(Bitmap bitmap, Mypixel nestP){
        int y1 = nestP.y;
        LinkedList<Mypixel> pixelsY1 = new LinkedList<>();
        while (y1 < bitmap.getHeight() - 1 && bitmap.getPixel(nestP.x, y1) == Color.BLACK) {
            Mypixel p = new Mypixel(nestP.x, y1, bitmap.getPixel(nestP.x, y1));
            p.setVisited(true);
            pixelsY1.add(p);
            y1++;
        }
        int y2 = nestP.y;
        LinkedList<Mypixel> pixelsY2 = new LinkedList<>();
        while (y2 > 1 && bitmap.getPixel(nestP.x, y2) == Color.BLACK) {
            Mypixel p = new Mypixel(nestP.x, y2, bitmap.getPixel(nestP.x, y2));
            p.setVisited(true);
            pixelsY2.add(p);
            y2--;
        }
        Path path = new Path();
        path.moveTo(pixelsY2.getLast().x, pixelsY2.getLast().y);
        path.lineTo(pixelsY1.getLast().x, pixelsY1.getLast().y);
        floodFillPath.addPath(path);

        postElaboration.addAll(pixelsY1);
        postElaboration.addAll(pixelsY2);
        Log.e(VFD_LOG,"postElaboration size: "+postElaboration.size());
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));

            scalePointX = detector.getFocusX();
            scalePointY = detector.getFocusY();

            Log.e("detector","Detector x : "+scalePointX + " y : "+scalePointY);

            invalidate();
            return true;
        }

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

    //da vedere se tenere solo questo metodo o anche il getmBitmap()

    public Bitmap getmSizedBitmap(){
        return Bitmap.createScaledBitmap(mBitmap,720,1280,false);
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
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

    public void setEditorFragment(EditorFragment editorFragment) {
        this.editorFragment = editorFragment;
    }

    public Bitmap getmTextureBitmapVFD() {
        return mTextureBitmapVFD;
    }

    public void setmTextureBitmapVFD(Bitmap mTextureBitmapVFD) {
        this.mTextureBitmapVFD = mTextureBitmapVFD;
    }

    public List<Point> getProspectPoitList() {
        return prospectPoitList;
    }

    public void setProspectPoitList(List<Point> prospectPoitList) {
        this.prospectPoitList = prospectPoitList;
    }

    public void setCannyMat(Mat cannyMat) {
        this.cannyMat = cannyMat;
    }

    public void finallyDraw() {
        for (Path p : myPathUndo) {
            Paint paint = pathColorMap.get(p);
            mCanvas.drawPath(p, paint);
        }
        mCanvas.drawPath(mPath,mPaint);
    }

}

