package com.filippowallandfloorv4.wallandfloorv4;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

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

    private static App app;
    private  Context context;
    private  static ImageDb imageDb;

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

    public static File getMainDir() {
        return mainDir;
    }

    public static List<String> getListProjectText() {
        return listProjectText;
    }

    public static List<String> getListZoneText() {
        return listZoneText;
    }
}
/** TODO: 17/10/2015 importare l'activity di photoEditor e farla funzionare come prima
 *  todo aggiungere il PickColor e i texture
 *  todo Aggiungere alla wafImage l'attributo Edited in boolean e farlo spiccare nelle gridViewPrew
 *  todo aggiungi photo da pulsante alwaysOnscreen
 *  todo ed Infine analizzare i pixel di un bitmap per vedere come fare a trovare le pareti in automatico QUI TI CI VOGLIO**/
