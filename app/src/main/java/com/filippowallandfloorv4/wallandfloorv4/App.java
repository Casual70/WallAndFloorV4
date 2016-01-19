package com.filippowallandfloorv4.wallandfloorv4;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filippo on 12/10/2015.
 */
public class App extends Application {

    public static final String LOG_APP = "App_log_debug";

    public static final String MAIN_DIR = "/WallAndFloor";
    public static File mainDir;
    public static List<String> listProjectText;
    public static List<String> listZoneText;
    public static ArrayList<Integer>drawablesDefault;

    private static App app;
    private  Context context;
    private static ImageDb imageDb;

    public static App getAppIstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = this;
        imageDb = new ImageDb(context);
        listProjectText = new ArrayList<String>();
        listZoneText = new ArrayList<String>();
        drawablesDefault = loadDrawable();
        checkDirectoty();
    }

    public  Context getContext() {
        return context;
    }

    public  ImageDb getImageDb() {
        return imageDb;
    }

    private void checkDirectoty(){
        File mainDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+MAIN_DIR);
        if (mainDir.exists()){
            App.mainDir = mainDir;
            Log.e(LOG_APP, "dir esiste: " + App.mainDir.getAbsolutePath());
        }else{
            if (mainDir.mkdirs()){
                App.mainDir = mainDir;
                Log.e(LOG_APP,"dir creata: "+ App.mainDir.getAbsolutePath());
            }else{
                Log.e(LOG_APP,"dir main non esiste e non è stata creata ERRORE");
            }
        }
    }
    private void loadTextureDefault(){
        Bitmap asphalt = BitmapFactory.decodeResource(getResources(),R.drawable.asphalt);
        Bitmap brick = BitmapFactory.decodeResource(getResources(),R.drawable.brick);
        Bitmap brickBig = BitmapFactory.decodeResource(getResources(),R.drawable.brickbig);
        Bitmap concrete = BitmapFactory.decodeResource(getResources(),R.drawable.concrete);
        Bitmap squarestone = BitmapFactory.decodeResource(getResources(),R.drawable.squarestone);
        Bitmap stone = BitmapFactory.decodeResource(getResources(),R.drawable.stone);
        Bitmap stoneold = BitmapFactory.decodeResource(getResources(),R.drawable.stoneold);
        Bitmap wood = BitmapFactory.decodeResource(getResources(),R.drawable.wood);
        Bitmap wood2 = BitmapFactory.decodeResource(getResources(),R.drawable.wood2);
        Bitmap wood_1 = BitmapFactory.decodeResource(getResources(),R.drawable.wood_1);
        Bitmap wooddark = BitmapFactory.decodeResource(getResources(),R.drawable.wooddark);
        Bitmap wooden = BitmapFactory.decodeResource(getResources(),R.drawable.wooden);
        Bitmap woodh = BitmapFactory.decodeResource(getResources(),R.drawable.woodh);
        Bitmap woodpine = BitmapFactory.decodeResource(getResources(),R.drawable.woodpine);
        Bitmap woodwhitw = BitmapFactory.decodeResource(getResources(),R.drawable.woodwhitw);

    } // non va bene

    private ArrayList<Integer> loadDrawable(){
        drawablesDefault = new ArrayList<>();
        drawablesDefault.add(R.drawable.asphalt);
        drawablesDefault.add(R.drawable.brick);
        drawablesDefault.add(R.drawable.brickbig);
        drawablesDefault.add(R.drawable.concrete);
        drawablesDefault.add(R.drawable.squarestone);
        drawablesDefault.add(R.drawable.stone);
        drawablesDefault.add(R.drawable.stoneold);
        drawablesDefault.add(R.drawable.wood);
        drawablesDefault.add(R.drawable.wood2);
        drawablesDefault.add(R.drawable.wood_1);
        drawablesDefault.add(R.drawable.wooddark);
        drawablesDefault.add(R.drawable.wooden);
        drawablesDefault.add(R.drawable.woodh);
        drawablesDefault.add(R.drawable.woodpine);
        drawablesDefault.add(R.drawable.woodwhitw);
        return drawablesDefault;
    }


    public static File getMainDir() {
        return mainDir;
    }

    public static List<String> getListProjectText() {
        return listProjectText;
    }

    public static List<String> getListZoneText() {
        return listZoneText;
    }

    public static ArrayList<Integer> getDrawablesDefault() {
        return drawablesDefault;
    }

}
/** TODO: 17/10/2015 importare l'activity di photoEditor e farla funzionare come prima
 *  todo aggiungere il PickColor e i texture
 *  todo Aggiungere alla wafImage l'attributo Edited in boolean e farlo spiccare nelle gridViewPrew
 *  todo aggiungi photo da pulsante alwaysOnscreen
 *  todo ed Infine analizzare i pixel di un bitmap per vedere come fare a trovare le pareti in automatico QUI TI CI VOGLIO**/
