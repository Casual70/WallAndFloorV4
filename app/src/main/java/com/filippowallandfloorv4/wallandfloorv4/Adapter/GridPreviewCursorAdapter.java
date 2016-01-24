package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.Activity.MainActivity;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

/**
 * Created by Filippo on 21/10/2015.
 */
public class GridPreviewCursorAdapter extends CursorAdapter {

    private Cursor cursor;
    private ImageLayHolder holder;
    private LayoutInflater inflater;
    private BitmapFactory.Options opt;
    private ImageDb db;


    public GridPreviewCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.cursor = c;
        this.inflater = LayoutInflater.from(context);
        this.opt = new BitmapFactory.Options();
        this.db = App.getAppIstance().getImageDb();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.image_lay,parent,false);
        holder = new ImageLayHolder(retView);
        retView.setTag(holder);
        return retView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageLayHolder holder = (ImageLayHolder)view.getTag();
        final WafImage wafImage = db.getWafByDb(cursor.getInt(cursor.getColumnIndex(ImageDb.IMAGE_COL_ID)));
        ImageView imageView = holder.getImageView();
        opt.inSampleSize = 8;
        Bitmap bitImage = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(ImageDb.IMAGE_FILE_PATH)),opt);
        imageView.setImageBitmap(Bitmap.createBitmap(Bitmap.createScaledBitmap(bitImage,300,350,false)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)context;
                mainActivity.startdEditor(wafImage);
            }
        });
    }
}
