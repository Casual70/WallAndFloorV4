package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.view.View;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.R;

public class ImageLayHolder {
    private ImageView imageView;

    public ImageLayHolder(View view) {
        imageView = (ImageView) view.findViewById(R.id.imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
