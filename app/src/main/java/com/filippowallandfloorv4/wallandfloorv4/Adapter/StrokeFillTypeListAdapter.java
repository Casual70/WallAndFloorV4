package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;


public class StrokeFillTypeListAdapter extends BaseAdapter {

    private Paint.Style[] fillType = {Paint.Style.STROKE,Paint.Style.FILL_AND_STROKE,Paint.Style.FILL};
    private Paint mPaint;
    private LayoutInflater inflater;
    private RowStrokelistHolder holder;
    private String[] styleDescr;

    public StrokeFillTypeListAdapter (Context context,Paint mPaint){
        this.mPaint = mPaint;
        inflater = LayoutInflater.from(context);
        styleDescr = context.getResources().getStringArray(R.array.filling_Style);
    }

    @Override
    public int getCount() {
        return fillType.length;
    }

    @Override
    public Paint.Style getItem(int position) {
        return fillType[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_strokelist,parent,false);
            holder = new RowStrokelistHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (RowStrokelistHolder)convertView.getTag();
        }
        ImageView imageView = holder.getStrokeWidthImageView();
        TextView textView = holder.getStrokeWidthTextView();
        Bitmap fillImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(mPaint);
        paint.setStrokeWidth(mPaint.getStrokeWidth());
        paint.setStyle(getItem(position));
        Canvas canvas = new Canvas(fillImage);
        canvas.drawCircle(50,50,50,paint);
        canvas.drawBitmap(fillImage, 0, 0, paint);
        imageView.setImageBitmap(fillImage);
        textView.setText(styleDescr[position]);
        return convertView;
    }
}
