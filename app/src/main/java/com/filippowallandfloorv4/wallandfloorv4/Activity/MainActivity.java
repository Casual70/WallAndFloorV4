package com.filippowallandfloorv4.wallandfloorv4.Activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.filippowallandfloorv4.wallandfloorv4.Adapter.CursorProjectAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.GridPreviewCursorAdapter;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Fragment.ProjectPreviewFragment;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    //save restore
    private static final String KEY_PROJECT = "sel_Project";

    private App app;
    private ImageDb db;
    private WafImage tempWafImage;
    private String tempProjectName;
    private Button photoButton;
    private ListView projectListView;
    private DrawerLayout drawerLayout;
    private LinearLayout linearLayout;
    //private ArrayAdapter<String>adapterListProject;
    private CursorProjectAdapter cursorProjectAdapter;
    private GridPreviewCursorAdapter gridPreviewCursorAdapter;
    private String loadImagePath;

    private static final String LOG_MAINACTIVITY_DEBUG = "Log_main_activity_debug";

    public static final int REQUEST_PHOTO_CAPTURE = 10;
    public static final int REQUEST_PHOTO_EDITOR = 15;
    public static final int REQUEST_PHOTO_LOAD = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_MAINACTIVITY_DEBUG, "onCreate");
        setContentView(R.layout.listview_project_drawerlay);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLay_listProject);
        projectListView = (ListView)findViewById(R.id.ListView_Project);
        linearLayout = (LinearLayout)findViewById(R.id.linearLay_listProject);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(LOG_MAINACTIVITY_DEBUG, "onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_MAINACTIVITY_DEBUG, "onResume");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(LOG_MAINACTIVITY_DEBUG, "onStart");
        app = App.getAppIstance();
        db = app.getImageDb();
        Log.e(LOG_MAINACTIVITY_DEBUG, String.valueOf(db.getAllWafImages().size()));
        if (cursorProjectAdapter ==null){
            cursorProjectAdapter = new CursorProjectAdapter(app.getContext(),db.getAllByProjectCursor(),false);
        }
        projectListView.setAdapter(cursorProjectAdapter);
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameProject = cursorProjectAdapter.getItem(position);
                ProjectPreviewFragment fragment = new ProjectPreviewFragment();
                fragment.setProjectPreview(createPreview(app.getContext(), nameProject));
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.contentFrame, fragment).commit();
                Log.e(LOG_MAINACTIVITY_DEBUG, nameProject);
            }
        });
    }

    private GridView createPreview(Context context, String nameProject){ //usare un viewHolder anche qui Riciclare
        tempProjectName = nameProject;
        GridView gridView = new GridView(app.getContext());
        gridView.setNumColumns(2);
        gridPreviewCursorAdapter = new GridPreviewCursorAdapter(this,db.getAllWafImageSortByProjectCursor(nameProject),true); // by Cursor
        gridView.setAdapter(gridPreviewCursorAdapter);
        gridView.setEmptyView(LayoutInflater.from(app.getContext()).inflate(R.layout.empty_grid_view, null));
        return gridView;
    }
    public void startdEditor(WafImage wafImage){
        Intent editorIntent = new Intent(EditorActivity.EDITOR_ACTION);
        editorIntent.putExtra(EditorActivity.EXTRA_WAF_IMAGE, wafImage);
        startActivityForResult(editorIntent, REQUEST_PHOTO_EDITOR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_listProject){
            drawerLayout.openDrawer(linearLayout);
            invalidateOptionsMenu();
        }
        if (id == R.id.action_takephoto){
            takeNewPhoto(item.getActionView());
        }
        if (id == R.id.action_loadPhoto){
            loadImage();
        }

        return super.onOptionsItemSelected(item);
    }
    public void takeNewPhoto(View v){
        Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoCaptureIntent.resolveActivity(getPackageManager())!=null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
            String imageFilename = "JPEG_"+timeStamp+" _ ";
            File dir = App.getMainDir();
            try {
                File imageFile = File.createTempFile(imageFilename,".jpeg",dir);
                tempWafImage = new WafImage(imageFile.getAbsoluteFile());
                photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempWafImage.getFilePath()));
                startActivityForResult(photoCaptureIntent, REQUEST_PHOTO_CAPTURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void loadImage(){
        Intent loadPhoto = new Intent();
        loadPhoto.setAction(Intent.ACTION_GET_CONTENT);
        loadPhoto.setType("image/*");
        startActivityForResult(Intent.createChooser(loadPhoto, "Select Picture"), REQUEST_PHOTO_LOAD);
    }
    private boolean savePikedPhoto(String loadImagePath, Bitmap image){
        File imageFile = null;
        WafImage wafImage = null;
        String externalSuffix = "imported.jpeg";
        boolean isSaved = false;
        Bitmap bitmap = image;//critico
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        String imageFilename = "JPEG_"+timeStamp+" _ ";
        File dir = App.getMainDir();
        FileOutputStream out = null;
        try {
            imageFile = File.createTempFile(imageFilename,externalSuffix, dir);
            out = new FileOutputStream(imageFile.getAbsolutePath());
            if (bitmap==null){
                Log.e("DIO CANE", "PORCO");
                return false;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (out != null){
                    out.close();
                    isSaved = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSaved =false;
            }
        }
        if (isSaved){
            wafImage = new WafImage(imageFile.getAbsoluteFile());
            this.loadImagePath = wafImage.getFilePath().toString();
        }else{
            isSaved = false;
        }
        return isSaved;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO_CAPTURE){
            if (resultCode == RESULT_OK){
                dialogTakedPhoto(tempWafImage);
            }else if(resultCode == RESULT_CANCELED){
                Log.e(LOG_MAINACTIVITY_DEBUG,"REQUEST_PHOTO_CAPTURE : CANCELLED");
            }else{
                Log.e(LOG_MAINACTIVITY_DEBUG,"REQUEST_PHOTO_CAPTURE : NO OK && NO CANCELLED, Error");
            }
        }
        if (requestCode == REQUEST_PHOTO_EDITOR){
            gridPreviewCursorAdapter.swapCursor(db.getAllWafImageSortByProjectCursor(tempProjectName));
        }
        if (requestCode == REQUEST_PHOTO_LOAD){
            Log.e(LOG_MAINACTIVITY_DEBUG, "REQUEST_LOAD_PHOTO Result code "+REQUEST_PHOTO_LOAD);
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                loadImagePath = selectedImageUri.getPath();
                Bitmap bit = null;
                try {
                    bit = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (savePikedPhoto(loadImagePath,bit)){
                    File f = new File(loadImagePath);
                    WafImage loadedImage = new WafImage(f);
                    dialogTakedPhoto(loadedImage);
                }
                Log.e(LOG_MAINACTIVITY_DEBUG, "REQUEST_LOAD_PHOTO "+loadImagePath);
            }else if (resultCode == RESULT_CANCELED){
                Log.e(LOG_MAINACTIVITY_DEBUG,"REQUEST_PHOTO_LOAD : CANCELLED");
            }

        }else{
            tempWafImage=null;
        }
    }
    private void dialogTakedPhoto(final WafImage wafImage){
        final Dialog dialog = new Dialog(this,R.style.AppTheme);
        View dialogView = LayoutInflater.from(app.getContext()).inflate(R.layout.dialog_project_zone,null);
        dialog.setContentView(dialogView, new ViewGroup.LayoutParams(650, 600));
        final EditText projectName = (EditText)dialogView.findViewById(R.id.ProjectName_edit);
        final EditText zoneName = (EditText)dialogView.findViewById(R.id.ZoneName_edit);
        final ListView listView = (ListView)dialogView.findViewById(R.id.listView_dialog);
        listView.setAdapter(cursorProjectAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                projectName.setText(cursorProjectAdapter.getItem(position));
            }
        });
        TextView filePath = (TextView)dialogView.findViewById(R.id.FilePath_edit);
        Button confirm = (Button)dialogView.findViewById(R.id.button_confim);
        Button avoid = (Button)dialogView.findViewById(R.id.button_avoid);
        filePath.setText(wafImage.getFilePath().getAbsolutePath());
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable nameProj = projectName.getText();
                Editable nameZone = zoneName.getText();
                if (TextUtils.isEmpty(nameProj)||TextUtils.isEmpty(nameZone)){
                    if (TextUtils.isEmpty(nameProj)){
                        Toast.makeText(MainActivity.this, "nameProj Vuoto", Toast.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(nameZone)){
                        Toast.makeText(MainActivity.this, "nameZone Vuoto", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (nameProj.toString().equalsIgnoreCase(nameZone.toString())){
                        Toast.makeText(MainActivity.this, "Project e Zona Devono avere nomi diversi", Toast.LENGTH_SHORT).show();
                    } else {
                        wafImage.setNomeProject(nameProj.toString());
                        wafImage.setNomeZona(nameZone.toString());
                        db.addWafToDb(wafImage);
                        Log.e(LOG_MAINACTIVITY_DEBUG, "WafImage aggiunto ad DB :" + wafImage.getNomeProject() + " " + wafImage.getNomeZona());
                        cursorProjectAdapter.swapCursor(db.getAllByProjectCursor());
                        if (gridPreviewCursorAdapter == null){
                            gridPreviewCursorAdapter = new GridPreviewCursorAdapter(app.getContext(),db.getAllWafImageSortByProjectCursor(nameProj.toString()),true);
                        }
                        gridPreviewCursorAdapter.swapCursor(db.getAllWafImageSortByProjectCursor(nameProj.toString()));
                        dialog.dismiss();
                    }
                }
            }
        });
        avoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PROJECT,tempProjectName);
        Log.e(LOG_MAINACTIVITY_DEBUG,"onsave");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            Log.e(LOG_MAINACTIVITY_DEBUG, "onrestore");
            ProjectPreviewFragment fragment = new ProjectPreviewFragment();
            fragment.setProjectPreview(createPreview(app.getContext(), savedInstanceState.getString(KEY_PROJECT)));
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.contentFrame, fragment).commit();
        }
    }
}


//** nel onActivityResult aprire un dialog che permetta di integrare la waf image con i dati
// riguardanti il nome del progetto e la zona, con una lista di scelte tra quelli già esistenti o nuovi