package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;

public class DialogTextureElementHolder {
    private ImageView imageViewTexture;
    private TextView nameTexture;

    public DialogTextureElementHolder(View view) {
        imageViewTexture = (ImageView) view.findViewById(R.id.imageView_texture);
        nameTexture = (TextView) view.findViewById(R.id.name_Texture);
    }

    public ImageView getImageViewTexture() {
        return imageViewTexture;
    }

    public TextView getNameTexture() {
        return nameTexture;
    }
}
