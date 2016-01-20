package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

/**
 * Created by Filippo on 19/01/2016.
 */
public class DialogTextureCustomGridAdapter extends CursorAdapter {

    private Cursor cursor;
    private DialogTextureElementHolder holder;
    private LayoutInflater inflater;
    private BitmapFactory.Options opt;
    private ImageDb db;

    public DialogTextureCustomGridAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.cursor = cursor;
        this.inflater = LayoutInflater.from(context);
        this.opt = new BitmapFactory.Options();
        this.db = App.getAppIstance().getImageDb();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_texture_element,parent,false);
        holder = new DialogTextureElementHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DialogTextureElementHolder holder = (DialogTextureElementHolder)view.getTag();
        if (cursor == null || cursor.isLast()){
            ImageView imageView = holder.getImageViewTexture();
            if (imageView.getDrawable()!=null){
                Log.e("immagine non null","non null");
            }
        }else{
            WafImage wafImageTexture = db.getWafByDb(cursor.getInt(cursor.getColumnIndex(ImageDb.IMAGE_COL_ID)));
            ImageView imageView = holder.getImageViewTexture();
            opt.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(wafImageTexture.getFilePath().getPath(),opt); // da verificare
            // aggiungere create scaled bitmap ?
            imageView.setImageBitmap(bitmap);
        }
    }
}
