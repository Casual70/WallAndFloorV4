package com.filippowallandfloorv4.wallandfloorv4.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeFillTypeListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeWidthListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Fragment.EditorFragment;
import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.rarepebble.colorpicker.ColorPickerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditorActivity extends Activity {

    public static final String EDITOR_ACTION = "com.filippowallandfloorv4.wallandfloorv4.action.EDITOR_ACTION";
    public static final String EXTRA_WAF_IMAGE = "com.filippowallandfloorv4.wallandfloorv4.extra.EXTRA_WAF_IMAGE";

    public static final String LOG_EditorActivity = "EditorActivity_Debug";

    private WafImage wafImage;
    private ViewForDrawIn vfd;
    private DrawerLayout drawerLayout_color;
    private App app;
    private EditorFragment fragment;

    private Bitmap myBitmap;
    private Paint myPaint;
    private Button confirmColor;
    private Button strokeButton,strokeTypeButton,grayScaleButton;
    private ToggleButton freeHandButtton, oneLineButton;
    private RadioGroup toggleGroup;
    private ColorPickerView cpv;
    private ListView strokeWidthList,strokeStyleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_EditorActivity, "onCreate");
        wafImage = getIntent().getParcelableExtra(EXTRA_WAF_IMAGE);
        app = App.getAppIstance();
        setContentView(R.layout.drawerlay);
        drawerLayout_color = (DrawerLayout)findViewById(R.id.drawerLayout_color);
        confirmColor = (Button)findViewById(R.id.button_confim);
        freeHandButtton = (ToggleButton)findViewById(R.id.button_freeHand);
        strokeButton = (Button)findViewById(R.id.button_stroke);
        strokeTypeButton = (Button)findViewById(R.id.button_strokeType);
        oneLineButton = (ToggleButton)findViewById(R.id.button_oneLine);
        grayScaleButton = (Button)findViewById(R.id.grayScale_button);
        cpv = (ColorPickerView)findViewById(R.id.colorPicker);
        strokeWidthList = (ListView)findViewById(R.id.listView_strokewidth);
        strokeStyleList = (ListView)findViewById(R.id.listView_strokeStyle);
        toggleGroup = (RadioGroup)findViewById(R.id.toggleGroup);
        myPaint = initPaint(myPaint);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        myBitmap = decodeInSample(wafImage);
        //vfd = new ViewForDrawIn(app.getContext(),myPaint,myBitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_EditorActivity, "onStart");
        fragment = new EditorFragment();
        //fragment.setVfd(vfd);
        fragment.setmBitmapAndPaint(myBitmap, myPaint);
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
        toggleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ToggleButton toggleButton = (ToggleButton) group.getChildAt(i);
                    toggleButton.setChecked(toggleButton.getId() == checkedId);
                    fragment.getVfd().setFreeHand(freeHandButtton.isChecked());
                    fragment.getVfd().setOneLine(oneLineButton.isChecked());
                }
            }
        });

        strokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWidthList.setAdapter(new StrokeWidthListAdapter(app.getContext(), myPaint));
                strokeWidthList.setVisibility(View.VISIBLE);
                freeHandButtton.setChecked(true);

            }
        });
        strokeTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeStyleList.setAdapter(new StrokeFillTypeListAdapter(app.getContext(),myPaint));
                strokeStyleList.setVisibility(View.VISIBLE);
                freeHandButtton.setChecked(true);
            }
        });
        strokeWidthList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer i = (Integer) parent.getItemAtPosition(position);
                myPaint.setStrokeWidth(i.floatValue());
                strokeWidthList.setVisibility(View.GONE);
                drawerLayout_color.closeDrawers();
            }
        });
        strokeStyleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paint.Style style = (Paint.Style) parent.getItemAtPosition(position);
                myPaint.setStyle(style);
                strokeStyleList.setVisibility(View.GONE);
                drawerLayout_color.closeDrawers();
            }
        });
        grayScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.getVfd().grayscale();
            }
        });
    }
    public void onToggle(View v){
        ((RadioGroup)v.getParent()).check(v.getId());
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
        opt.inSampleSize = 3;
        opt.inMutable = true;
        Bitmap imageBit = BitmapFactory.decodeFile(wafImage.getFilePath().getAbsolutePath(), opt);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap ultimate = Bitmap.createScaledBitmap(imageBit, size.x, size.y, false);
        return ultimate;
    }
    private boolean saveCurrentPhoto(WafImage wafImage){
        String editedSuffix = "_edit.jpeg";
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

    public ToggleButton getFreeHandButtton() {
        return freeHandButtton;
    }
}
