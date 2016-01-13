package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.R;

import java.util.ArrayList;

/**
 * Created by Filippo on 13/01/2016.
 */
public class ColorGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer>colors;
    private ColorsPalletteHolder holder;
    private LayoutInflater inflater;
    private SharedPreferences preferences;

    public ColorGridAdapter(Context context, ArrayList<Integer> colors,SharedPreferences preferences) {
        this.context = context;
        this.colors = colors;
        this.preferences = preferences;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Integer getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = inflater.inflate(R.layout.colors_pallette,null);
            holder = new ColorsPalletteHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ColorsPalletteHolder)convertView.getTag();
        }
        ImageButton color = holder.getImageButton();
        ImageView bar = holder.getImageView2();

        Bitmap colorBit = Bitmap.createBitmap(color.getWidth() - 5, color.getHeight() - 5, Bitmap.Config.ARGB_8888);
        Bitmap barBit = Bitmap.createBitmap(bar.getWidth(),bar.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setColor(getItem(position));

        Canvas colorCanv = new Canvas(colorBit);
        Canvas barCanv = new Canvas(barBit);

        colorCanv.drawRect(colorCanv.getClipBounds(),paint);

        return convertView;
    }
}
