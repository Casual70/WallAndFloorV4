package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeFillTypeListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeWidthListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditorFragment extends android.app.Fragment implements View.OnClickListener {

    private final String LOG_EDITFRAG = "EditorFragment";

    public ViewForDrawIn vfd;
    public LinearLayout bottomActionBar;
    public ImageButton openIB, strokeWidthIB, strokeStyleIB, grayScaleIB,textureIB,saveIB;
    public ToggleButton freeHandToggleB, oneLineToggleB;
    public ListView strokeWidthList,strokeStyleList;
    public RadioGroup toggleGroup;
    public Bitmap mBitmap;
    public Paint mPaint;

    public EditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("fragment", "OncreateView");
        View view = inflater.inflate(R.layout.prova2,null);
        vfd = (ViewForDrawIn)view.findViewById(R.id.ViewForDrawIn);
        vfd.setmBitmap(mBitmap);
        vfd.setmPaint(mPaint);
        strokeStyleIB = (ImageButton)view.findViewById(R.id.strokeStyleImageButton);
        strokeWidthIB = (ImageButton)view.findViewById(R.id.strokeWidthImageButton);
        bottomActionBar = (LinearLayout)view.findViewById(R.id.action_bar_layout);
        toggleGroup = (RadioGroup)view.findViewById(R.id.toggleGroup_bottomActionBar);
        freeHandToggleB = (ToggleButton)view.findViewById(R.id.freeHandImageButton);
        oneLineToggleB = (ToggleButton)view.findViewById(R.id.oneLineImageButton);
        openIB = (ImageButton)view.findViewById(R.id.openImageButton);
        grayScaleIB = (ImageButton)view.findViewById(R.id.grayScaleImageButton);
        strokeWidthList = (ListView)view.findViewById(R.id.listView_strokewidth_icon);
        strokeStyleList = (ListView)view.findViewById(R.id.listView_strokeStyle_icon);
        return view;
    }

    @Override
    public void onClick(View v) {
        ((RadioGroup)v.getParent()).check(v.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        freeHandToggleB.setOnClickListener(this);
        oneLineToggleB.setOnClickListener(this);
        strokeWidthIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeWidthList.setAdapter(new StrokeWidthListAdapter(getActivity(), vfd.getmPaint()));
                strokeWidthList.setVisibility(View.VISIBLE);
                freeHandToggleB.setChecked(true);
            }
        });
        strokeStyleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeStyleList.setAdapter(new StrokeFillTypeListAdapter(getActivity(),vfd.getmPaint()));
                strokeStyleList.setVisibility(View.VISIBLE);
                freeHandToggleB.setChecked(true);
            }
        });


        toggleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ToggleButton toggleButton = (ToggleButton) group.getChildAt(i);
                    toggleButton.setChecked(toggleButton.getId() == checkedId);
                    vfd.setFreeHand(freeHandToggleB.isChecked());
                    vfd.setOneLine(oneLineToggleB.isChecked());
                }
            }
        });
        grayScaleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vfd.grayscale(); // da finire di sistemare nella View for Draw In è necessario ridipigere il Canvas
            }
        });
        openIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActionBarBottom();
            }
        });

    }

    public void openActionBarBottom(){

        if (bottomActionBar.getHeight()<=49){ // chiuso/aperto
            bottomActionBar.getLayoutParams().height = 90;
            bottomActionBar.requestLayout();
            Log.e(LOG_EDITFRAG,"chiuso/aperto");
        }else{                                // aperto/chiuso
            bottomActionBar.getLayoutParams().height = 25;
            bottomActionBar.requestLayout();
            Log.e(LOG_EDITFRAG,"aperto/chiuso");
        }
    }

    public void setVfd(ViewForDrawIn vfd) {
        this.vfd = vfd;
    }

    public ViewForDrawIn getVfd() {
        return vfd;
    }

    public void setmBitmapAndPaint(Bitmap mBitmap,Paint mPaint){
        this.mBitmap = mBitmap;
        this.mPaint = mPaint;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public Paint getmPaint() {
        return mPaint;
    }


}
