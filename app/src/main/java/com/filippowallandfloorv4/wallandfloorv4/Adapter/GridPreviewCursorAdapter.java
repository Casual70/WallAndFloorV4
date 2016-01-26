package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.Activity.MainActivity;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;
import com.squareup.picasso.Picasso;

/**
 * Created by Filippo on 21/10/2015.
 */
public class GridPreviewCursorAdapter extends CursorAdapter {

    private Cursor cursor;
    private ImageLayHolder holder;
    private LayoutInflater inflater;
    private BitmapFactory.Options opt;
    private ImageDb db;
    private Point size;


    public GridPreviewCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.cursor = c;
        this.inflater = LayoutInflater.from(context);
        this.opt = new BitmapFactory.Options();
        this.db = App.getAppIstance().getImageDb();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        this.size = new Point();
        display.getSize(size);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.image_lay,parent,false);
        holder = new ImageLayHolder(retView);
        retView.setTag(holder);
        return retView;
    }

    //per riciclare provare a mettere la view come global

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        holder = (ImageLayHolder)view.getTag();
        final WafImage wafImage = db.getWafByDb(cursor.getInt(cursor.getColumnIndex(ImageDb.IMAGE_COL_ID)));
        ImageView imageView = holder.getImageView();
        opt.inJustDecodeBounds = true;
        Bitmap bitImage = BitmapFactory.decodeFile(wafImage.getFilePath().getAbsolutePath(),opt);
        //imageView.setImageBitmap(Bitmap.createBitmap(Bitmap.createScaledBitmap(bitImage,300,350,false)));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.startdEditor(wafImage);
            }
        });
        Picasso.
                with(context).
                load(wafImage.getFilePath()).
                noPlaceholder().
                resize(720 / 2, 1280 / 2).
                onlyScaleDown().
                tag(this.holder).
                into(imageView);
        Log.e("Log adapterGrid", "decoded bitmap size : h : " + opt.outHeight + " w : " + opt.outWidth);
    }

}
