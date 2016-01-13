package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.filippowallandfloorv4.wallandfloorv4.R;

public class ColorsPalletteHolder {
    private ImageButton imageButton;
    private ImageView imageView2;

    public ColorsPalletteHolder(View view) {
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        imageView2 = (ImageView) view.findViewById(R.id.imageView2);
    }

    public ImageView getImageView2() {
        return imageView2;
    }

    public ImageButton getImageButton() {
        return imageButton;
    }
}
