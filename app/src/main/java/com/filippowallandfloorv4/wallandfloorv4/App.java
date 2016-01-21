package com.filippowallandfloorv4.wallandfloorv4;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
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
    public static final String TEXTURE_DIR = MAIN_DIR+"/Texture";
    public static File mainDir;
    public static File textureDir;
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
        File textureDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+TEXTURE_DIR);
        if (mainDir.exists() && textureDir.exists()){
            App.mainDir = mainDir;
            App.textureDir = textureDir;
            Log.e(LOG_APP, "Directory check ok MainDir e TextureDir");
        }else{
            if (mainDir.mkdirs() && textureDir.mkdirs()){
                App.mainDir = mainDir;
                App.textureDir = textureDir;
                Log.e(LOG_APP,"Directory create ok MainDir e TextureDir");
            }else{
                Log.e(LOG_APP,"dir main non esiste e non è stata creata ERRORE");
            }
        }
    }

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
