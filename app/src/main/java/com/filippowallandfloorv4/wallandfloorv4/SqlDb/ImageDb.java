package com.filippowallandfloorv4.wallandfloorv4.SqlDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filippo on 11/10/2015.
 */
public class ImageDb extends SQLiteOpenHelper {

    public static final String LOG_IMAGEDB = "imageDb_DEGUB";

    public static final String DB_NAME = "WallAndFloorMain_db";
    public static final String TABLE_WAFIMAGE = "table_WafImage";
    public static final int DB_VERSION = 1;

    public static final String IMAGE_COL_ID = "_id";
    public static final String IMAGE_FILE_PATH = "path_image";
    public static final String NAME_PROJECT = "project_name";
    public static final String NAME_ZONE = "zone_name";

    public static final String TEXTURE_CODE = "texture_code";

    private Context context;

    public ImageDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TALE = "CREATE TABLE "+TABLE_WAFIMAGE + "("+
                IMAGE_COL_ID + " INTEGER PRIMARY KEY," +
                IMAGE_FILE_PATH + " TEXT," +
                NAME_PROJECT + " TEXT," +
                NAME_ZONE + " TEXT," +
                TEXTURE_CODE + " INTEGER"+
                ")";
        db.execSQL(CREATE_TALE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIXSTS " + TABLE_WAFIMAGE);
        onCreate(db);
    }
    public void addWafToDb(WafImage image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_ZONE, image.getNomeZona()); Log.e("zone", image.getNomeZona());
        values.put(IMAGE_FILE_PATH, image.getFilePath().getAbsolutePath());
        values.put(NAME_PROJECT,image.getNomeProject());Log.e("project", image.getNomeProject());
        values.put(TEXTURE_CODE,image.getTextureCode());Log.e("Textture code", "" + image.getTextureCode());
        db.insert(TABLE_WAFIMAGE, null, values);
        db.close();
    }
    public WafImage getWafByDb (int id) {
        WafImage wafImage;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WAFIMAGE, new String[]{IMAGE_COL_ID, IMAGE_FILE_PATH, NAME_PROJECT, NAME_ZONE, TEXTURE_CODE}, IMAGE_COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        if (cursor!=null){
            File path = new File(cursor.getString(cursor.getColumnIndex(IMAGE_FILE_PATH)));
            wafImage = new WafImage(   path,
                                                cursor.getString(cursor.getColumnIndex(NAME_PROJECT)),
                                                cursor.getString(cursor.getColumnIndex(NAME_ZONE)),
                                                cursor.getInt(cursor.getColumnIndex(IMAGE_COL_ID)),
                                                cursor.getInt(cursor.getColumnIndex(TEXTURE_CODE)));
            Log.e(LOG_IMAGEDB,"WAFIMAGE, creta correttamente e restituita");
        }else{
            wafImage = null;
            Log.e(LOG_IMAGEDB,"WAFIMAGE, errore nella creazione di waf image: null");
        }
        return wafImage;

    }
    public List<WafImage>getAllWafImages(){
        List<WafImage> listWafImages = new ArrayList<WafImage>();
        String allSelectQuery = "SELECT * FROM "+TABLE_WAFIMAGE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(allSelectQuery, null);
        if (cursor.moveToFirst()){
            do{
                File path = new File(cursor.getColumnName(cursor.getColumnIndex(IMAGE_FILE_PATH)));
                WafImage wafImage = new WafImage(   path,
                        cursor.getString(cursor.getColumnIndex(NAME_PROJECT)),
                        cursor.getString(cursor.getColumnIndex(NAME_ZONE)),
                        cursor.getInt(cursor.getColumnIndex(IMAGE_COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(TEXTURE_CODE)));
                listWafImages.add(wafImage);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listWafImages;
    }
    public int updateWafImage(WafImage image){ // not work for now
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IMAGE_FILE_PATH,image.getFilePath().getAbsolutePath());
        values.put(NAME_PROJECT, image.getNomeProject());
        values.put(NAME_ZONE, image.getNomeZona());
        return  db.update(TABLE_WAFIMAGE,values,IMAGE_COL_ID+" = ?",new String[]{String.valueOf(image.get_id())});
    }
    public void deleteWafImage(WafImage image){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WAFIMAGE, IMAGE_COL_ID + " = ?", new String[]{String.valueOf(image.get_id())});
        db.close();
    }
    public List<WafImage> getAllWafImageSortByProject(String projectName){ // not work for now
        SQLiteDatabase db = this.getReadableDatabase();
        List<WafImage>wafImageList = new ArrayList<>();
        String allSelectQuery = "SELECT * FROM "+TABLE_WAFIMAGE + " WHERE "+NAME_PROJECT+" =?";
        Cursor cursor = db.rawQuery(allSelectQuery, new String[]{projectName});
        if (cursor.moveToFirst()){
            do {
                File path = new File(cursor.getString(cursor.getColumnIndex(IMAGE_FILE_PATH)));
                WafImage wafImage = new WafImage(   path,
                        cursor.getString(cursor.getColumnIndex(NAME_PROJECT)),
                        cursor.getString(cursor.getColumnIndex(NAME_ZONE)),
                        cursor.getInt(cursor.getColumnIndex(IMAGE_COL_ID)));
                wafImageList.add(wafImage);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return wafImageList;
    }
    public ArrayList<String> getAllByProjectString(){ // not work for now
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+NAME_PROJECT+" FROM "+TABLE_WAFIMAGE + " GROUP BY "+NAME_PROJECT;
        ArrayList<String>projectList = new ArrayList<>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do{
                projectList.add(cursor.getString(cursor.getColumnIndex(NAME_PROJECT)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return projectList;
    }
    public ArrayList<String>getAllZoneByProject(String projectName){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String>zoneList = new ArrayList<>();
        Cursor cursor = db.query(false, TABLE_WAFIMAGE, new String[]{IMAGE_COL_ID, NAME_PROJECT, NAME_ZONE}, NAME_PROJECT + "=?", new String[]{projectName}, null, null, null, null);
        if (cursor.moveToFirst()){
            do{
                zoneList.add(cursor.getString(cursor.getColumnIndex(NAME_ZONE)));
                Log.e("zone load", cursor.getString(cursor.getColumnIndex(NAME_ZONE)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return zoneList;
    }
    public Cursor getAllZoneByProjectCursor(String projectName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(false, TABLE_WAFIMAGE, new String[]{IMAGE_COL_ID, NAME_PROJECT, NAME_ZONE}, NAME_PROJECT + "=?", new String[]{projectName}, NAME_ZONE , null, null, null);
    }
    public Cursor getAllWafImageByZone(String zoneName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(false, TABLE_WAFIMAGE, new String[]{IMAGE_COL_ID, NAME_PROJECT, NAME_ZONE,IMAGE_FILE_PATH}, NAME_ZONE + "=?", new String[]{zoneName}, null, null, null, null);
    }
    public boolean deleteAllWafImageByZone(String zoneName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getAllWafImageByZone(zoneName);
        if (cursor.moveToFirst()){
            do{
                File fildPath = new File(cursor.getString(cursor.getColumnIndex(IMAGE_FILE_PATH)));
                if (fildPath.delete()){
                    Toast.makeText(context, R.string.imageRemoved,Toast.LENGTH_SHORT).show();
                }
            }while (cursor.moveToNext());
        }
        db.delete(TABLE_WAFIMAGE,NAME_ZONE+"=?",new String[]{zoneName});
        return true;
    }

    public Cursor getAllByProjectCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(true, TABLE_WAFIMAGE, new String[]{IMAGE_COL_ID,NAME_PROJECT}, null, null, NAME_PROJECT, null, null, null);
    }
    public Cursor getAllWafImageSortByProjectCursor(String projectName){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(false,TABLE_WAFIMAGE,new String[]{IMAGE_COL_ID,NAME_PROJECT,NAME_ZONE,IMAGE_FILE_PATH},NAME_PROJECT+"=?",new String[]{projectName},null,null,null,null);
    }
    public boolean deleteAllWafImageByProject(String projectName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getAllWafImageSortByProjectCursor(projectName);
        if (cursor.moveToFirst()){
            do{
                File filePath = new File(cursor.getString(cursor.getColumnIndex(IMAGE_FILE_PATH)));
                if (filePath.delete()){
                    Toast.makeText(context,R.string.projectRemoved,Toast.LENGTH_SHORT).show();
                }
            }while (cursor.moveToNext());
        }
        db.delete(TABLE_WAFIMAGE,NAME_PROJECT+"=?",new String[]{projectName});
        return true;
    }
    public Cursor getAllWafImageTexture(){ // da collaudare
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(false,TABLE_WAFIMAGE,new String[]{IMAGE_COL_ID,NAME_PROJECT,NAME_ZONE,IMAGE_FILE_PATH,TEXTURE_CODE},TEXTURE_CODE+"=?",new String[]{String.valueOf(WafImage.TEXTURECODE_ON)},null,null,null,null);
    }
    public Cursor getAllWafImageCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(false,TABLE_WAFIMAGE,null,null,null,null,null,null,null);
    }

}
