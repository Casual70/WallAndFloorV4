package com.filippowallandfloorv4.wallandfloorv4.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Fragment.EditorFragment;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.rarepebble.colorpicker.ColorPickerView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditorActivity extends Activity {

    public static final String EDITOR_ACTION = "com.filippowallandfloorv4.wallandfloorv4.action.EDITOR_ACTION";
    public static final String EXTRA_WAF_IMAGE = "com.filippowallandfloorv4.wallandfloorv4.extra.EXTRA_WAF_IMAGE";

    public static final String LOG_EditorActivity = "EditorActivity_Debug";

    private WafImage wafImage;
    private DrawerLayout drawerLayout_color;
    private App app;
    private EditorFragment fragment;

    private SharedPreferences colorPref;
    private static final String keycolor2 = "col2";
    private static final String keycolor3 = "col3";
    private static final String keycolor4 = "col4";
    private static final String keycolor5 = "col5";
    private static final String keycolor6 = "col6";
    private static final String keycolor7 = "col7";
    private static final String keycolor8 = "col8";
    private static final String keycolor9 = "col9";
    private static final String[] keys = {keycolor2,keycolor3,keycolor4,keycolor5,keycolor6,keycolor7,keycolor8,keycolor9};

    private Bitmap myBitmap;
    private Paint myPaint;
    private Button confirmColor;
    private Button undoPathButton, redoPathButton;
    private Button onlyBorderButton;
    private ColorPickerView cpv;

    private Bitmap colorCelBit;

    private ImageButton color2,color3,color4,color5,color6,color7,color8,color9;
    private View.OnClickListener colorsListener;
    private ArrayList<ImageButton>colorsArray;

    private GridView gridViewColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_EditorActivity, "onCreate");
        wafImage = getIntent().getParcelableExtra(EXTRA_WAF_IMAGE);
        app = App.getAppIstance();
        setContentView(R.layout.drawerlay);
        drawerLayout_color = (DrawerLayout)findViewById(R.id.drawerLayout_color);
        confirmColor = (Button)findViewById(R.id.button_confim);
        undoPathButton = (Button)findViewById(R.id.button_undoPath);
        redoPathButton = (Button)findViewById(R.id.button_redoPath);
        onlyBorderButton = (Button)findViewById(R.id.button_onlyBord);
        cpv = (ColorPickerView)findViewById(R.id.colorPicker);
        colorPref = getSharedPreferences("colorPreference",MODE_PRIVATE);

        color2 = (ImageButton)findViewById(R.id.imageButton2);
        color3 = (ImageButton)findViewById(R.id.imageButton3);
        color4 = (ImageButton)findViewById(R.id.imageButton4);
        color5 = (ImageButton)findViewById(R.id.imageButton5);
        color6 = (ImageButton)findViewById(R.id.imageButton6);
        color7 = (ImageButton)findViewById(R.id.imageButton7);
        color8 = (ImageButton)findViewById(R.id.imageButton8);
        color9 = (ImageButton)findViewById(R.id.imageButton9);
        colorsArray = new ArrayList<>(8);
        colorsArray.add(color2);colorsArray.add(color3);colorsArray.add(color4);
        colorsArray.add(color5);colorsArray.add(color6);colorsArray.add(color7);
        colorsArray.add(color8);colorsArray.add(color9);

        colorCelBit = BitmapFactory.decodeResource(getResources(),R.drawable.floppy_icon);
        Log.e(LOG_EditorActivity,"primary bitmap"+colorCelBit.getHeight());

        cpv.showHex(false);
        cpv.showAlpha(true);
        myPaint = initPaint(myPaint);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;

        myBitmap = decodeInSample(wafImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_EditorActivity, "onStart");
        fragment = new EditorFragment();
        fragment.setmBitmapAndPaint(myBitmap, myPaint);
        fragment.setWafImage(wafImage);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.contentFrame_color,fragment).commit();
        confirmColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = cpv.getColor();
                Paint vfdPaint = fragment.getVfd().getmPaint();
                vfdPaint.setColor(color);
                if (vfdPaint.getShader() != null) {
                    vfdPaint.setShader(null);
                }
                fragment.getVfd().setmPaint(vfdPaint);
                drawerLayout_color.closeDrawers();
                cpv.setOriginalColor(fragment.getVfd().getmPaint().getColor());
            }
        });

        undoPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.getVfd().onUndoPath();

            }
        });
        redoPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.getVfd().onRedoPath();
            }
        });
        onlyBorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.getVfd().findBord();
                drawerLayout_color.closeDrawers();
            }
        });
        colorsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton imageButton = (ImageButton)v;
                if (imageButton.isSelected()){
                    Bitmap bitmapColor = Bitmap.createBitmap(colorCelBit.getWidth(),colorCelBit.getHeight(), Bitmap.Config.ARGB_8888);
                    Log.e(LOG_EditorActivity,"on click widht "+(bitmapColor.getWidth()));
                    Canvas colorcanvas = new Canvas(bitmapColor);
                    Paint paint = new Paint();
                    paint.setColor(cpv.getColor());
                    colorcanvas.drawCircle(colorcanvas.getClipBounds().centerX(), colorcanvas.getClipBounds().centerY(), colorCelBit.getHeight()/2-10, paint);
                    imageButton.setImageBitmap(bitmapColor);
                    String tag = (String)imageButton.getTag();
                    colorPref.edit().putInt(tag,paint.getColor()).apply();
                }else{
                    for (ImageButton ima:colorsArray){
                        ima.setSelected(false);
                    }
                    imageButton.setSelected(true);
                }

            }
        };
        for (int i = 0; i < colorsArray.size(); i++) {
            ImageButton ima = colorsArray.get(i);
            ima.setOnClickListener(colorsListener);
            ima.setSelected(false);
            ima.setTag(keys[i]);
        }
        for (int i = 0; i < colorsArray.size(); i++) {
            ImageButton ima = colorsArray.get(i);
            Log.e(LOG_EditorActivity,"weight,height "+ ima.getWidth()+ " "+ima.getHeight());
            Bitmap bitmapColor = Bitmap.createBitmap(colorCelBit.getWidth(),colorCelBit.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas colorcanvas = new Canvas(bitmapColor);
            Paint paint = new Paint();
            paint.setColor(colorPref.getInt(keys[i],Color.TRANSPARENT));
            colorcanvas.drawCircle(colorcanvas.getClipBounds().centerX(), colorcanvas.getClipBounds().centerY(), colorCelBit.getHeight()/2-10, paint);
            ima.setImageBitmap(bitmapColor);
        }
        DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                for(ImageButton ima: colorsArray){
                    if (ima.isSelected()){
                        int color = colorPref.getInt((String)ima.getTag(),Color.TRANSPARENT);
                        Paint vfdPaint = fragment.getVfd().getmPaint();
                        vfdPaint.setColor(color);
                        if (vfdPaint.getShader() != null) {
                            vfdPaint.setShader(null);
                        }
                        fragment.getVfd().setmPaint(vfdPaint);
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        drawerLayout_color.setDrawerListener(drawerListener);
    }


    @Override
    protected void onResume() {
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_save){
            if (saveCurrentPhoto(wafImage)){
                Toast.makeText(getApplicationContext(),R.string.save_success,Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.action_bit_setting){
            dialogSetBitmap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void dialogSetBitmap(){
        Dialog dialog = new Dialog(this);
        Bitmap bitmap = fragment.getVfd().getmBitmap();
        View view = getLayoutInflater().inflate(R.layout.photo_import_popup,null);
        SeekBar contrast = (SeekBar)view.findViewById(R.id.seekBar_contrast);
        dialog.setContentView(view);
        dialog.show();
    }

    private Paint initPaint(Paint mPaint){
        if (mPaint == null){
            mPaint = new Paint();
        }
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(50);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStrokeWidth(5);
        return mPaint;
    }
    private Bitmap decodeInSample(WafImage wafImage){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 1; // default is 6
        opt.inMutable = true;
        Bitmap imageBit = BitmapFactory.decodeFile(wafImage.getFilePath().getAbsolutePath(), opt);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap ultimate = Bitmap.createScaledBitmap(imageBit, size.x, size.y, false);
        return ultimate;
    }
    public boolean saveCurrentPhoto(WafImage wafImage){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        String editedSuffix = timeStamp+"_edit.jpeg";
        boolean isSaved = false;
        Bitmap edit = fragment.getVfd().getmBitmap();
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(wafImage.getFilePath().getAbsolutePath()+editedSuffix);
            edit.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
            isSaved = false;
        }finally {
            try{
                if (out !=null){
                    out.close();
                    isSaved = true;
                }
            }catch (IOException e){
                e.printStackTrace();
                isSaved = false;
            }
        }
        if (isSaved){
            File path = new File(wafImage.getFilePath().getAbsolutePath()+editedSuffix);
            WafImage savedWafImage = new WafImage(path);
            savedWafImage.setNomeZona(wafImage.getNomeZona());
            savedWafImage.setNomeProject(wafImage.getNomeProject());
            app.getImageDb().addWafToDb(savedWafImage);
        }else{
            isSaved = false;
        }
        return isSaved;
    }

    public WafImage getWafImage() {
        return wafImage;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                {
                    Log.e("OPENcV","OpenCV loaded successfully");
                }break;
                default:{
                    super.onManagerConnected(status);
                }break;
            }
        }
    };

}
