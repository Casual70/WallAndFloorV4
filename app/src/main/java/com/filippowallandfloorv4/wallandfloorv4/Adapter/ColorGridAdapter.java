package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;

import java.util.ArrayList;

/**
 * Created by Filippo on 13/01/2016.
 */
public class ColorGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> texture;
    private DialogTextureElementHolder holder;
    private LayoutInflater inflater;

    public ColorGridAdapter(Context context, ArrayList<Integer> texture) {
        this.context = context;
        this.texture = texture;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return texture.size();
    }

    @Override
    public Integer getItem(int position) {
        return texture.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = inflater.inflate(R.layout.dialog_texture_element,null);
            holder = new DialogTextureElementHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (DialogTextureElementHolder)convertView.getTag();
        }
        ImageView color = holder.getImageViewTexture();
        TextView bar = holder.getNameTexture();

        color.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),getItem(position)));

        return convertView;
    }
}
