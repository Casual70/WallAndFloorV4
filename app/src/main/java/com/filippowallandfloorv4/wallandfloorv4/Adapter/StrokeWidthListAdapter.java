package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;

/**
 * Created by Filippo on 12/11/2015.
 */
public class StrokeWidthListAdapter extends BaseAdapter {

    private int[] valueArray;
    private RowStrokelistHolder holder;
    private LayoutInflater inflater;
    private Paint mPaint;

    public StrokeWidthListAdapter(Context context,Paint mPaint){
        valueArray = context.getResources().getIntArray(R.array.stroke_dimension);
        inflater = LayoutInflater.from(context);
        this.mPaint = mPaint;
    }

    @Override
    public int getCount() {
        return valueArray.length;
    }

    @Override
    public Integer getItem(int position) {
        return valueArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = inflater.inflate(R.layout.row_strokelist,parent,false);
            holder = new RowStrokelistHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (RowStrokelistHolder) convertView.getTag();
        }
        ImageView imageView = holder.getStrokeWidthImageView();
        TextView textView = holder.getStrokeWidthTextView();
        Bitmap strokeimage = Bitmap.createBitmap(100, 50, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(mPaint);
        paint.setStrokeWidth((float) getItem(position));
        Canvas canvas = new Canvas(strokeimage);
        canvas.drawLine(0, 25, 100, 25, paint);
        canvas.drawBitmap(strokeimage, 0, 0, paint);
        imageView.setImageBitmap(strokeimage);
        textView.setText(String.valueOf(getItem(position)));
        return convertView;
    }
}
