package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;

public class RowStrokelistHolder {
    private TextView strokeWidthTextView;
    private ImageView strokeWidthImageView;

    public RowStrokelistHolder(View view) {
        strokeWidthTextView = (TextView) view.findViewById(R.id.stroke_width_TextView);
        strokeWidthImageView = (ImageView) view.findViewById(R.id.stroke_width_ImageView);
    }

    public ImageView getStrokeWidthImageView() {
        return strokeWidthImageView;
    }

    public TextView getStrokeWidthTextView() {
        return strokeWidthTextView;
    }
}
