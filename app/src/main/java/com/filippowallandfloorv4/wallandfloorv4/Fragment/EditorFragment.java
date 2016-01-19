package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.filippowallandfloorv4.wallandfloorv4.Activity.EditorActivity;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.ColorGridAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeFillTypeListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.StrokeWidthListAdapter;
import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.Model.ViewForDrawIn;
import com.filippowallandfloorv4.wallandfloorv4.Model.WafImage;
import com.filippowallandfloorv4.wallandfloorv4.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditorFragment extends android.app.Fragment implements View.OnClickListener {

    private final String LOG_EDITFRAG = "EditorFragment";

    public ViewForDrawIn vfd;
    public RelativeLayout bottomActionBar;
    public ImageButton openIB, strokeWidthIB, strokeStyleIB, grayScaleIB,textureIB,saveIB;
    public ToggleButton freeHandToggleB, oneLineToggleB;
    public ListPopupWindow listPopupStyle, listPopupLine;
    public RadioGroup toggleGroup;
    public Bitmap mBitmap;
    public Paint mPaint;
    public View[]listButton;
    private WafImage wafImage;

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
        vfd.setWafImage(wafImage);

        textureIB = (ImageButton)view.findViewById(R.id.textureImageButton);
        saveIB = (ImageButton)view.findViewById(R.id.saveimageButton);
        strokeStyleIB = (ImageButton)view.findViewById(R.id.strokeStyleImageButton);
        strokeWidthIB = (ImageButton)view.findViewById(R.id.strokeWidthImageButton);
        bottomActionBar = (RelativeLayout)view.findViewById(R.id.action_bar_layout);
        toggleGroup = (RadioGroup)view.findViewById(R.id.toggleGroup_bottomActionBar);
        freeHandToggleB = (ToggleButton)view.findViewById(R.id.freeHandImageButton);
        oneLineToggleB = (ToggleButton)view.findViewById(R.id.oneLineImageButton);
        openIB = (ImageButton)view.findViewById(R.id.openImageButton);
        grayScaleIB = (ImageButton)view.findViewById(R.id.grayScaleImageButton);
        listButton = new View[]{strokeStyleIB,strokeWidthIB,toggleGroup,grayScaleIB,textureIB,saveIB};

        return view;
    }

    @Override
    public void onClick(View v) {
        ((RadioGroup)v.getParent()).check(v.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        openActionBarBottom();
        freeHandToggleB.setOnClickListener(this);
        oneLineToggleB.setOnClickListener(this);
        strokeWidthIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupLine == null){
                    listPopupLine = new ListPopupWindow(getActivity());
                    listPopupLine.setAdapter(new StrokeWidthListAdapter(getActivity(),vfd.getmPaint()));
                    listPopupLine.setAnchorView(strokeWidthIB);
                    listPopupLine.setPromptPosition(ListPopupWindow.POSITION_PROMPT_ABOVE);
                    listPopupLine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Integer i = (Integer) parent.getItemAtPosition(position);
                            vfd.getmPaint().setStrokeWidth(i.floatValue());
                            listPopupLine.dismiss();
                        }
                    });
                    listPopupLine.show();
                }else{
                    if (listPopupLine.isShowing()){
                        listPopupLine.dismiss();
                    }else{
                        listPopupLine.show();
                        listPopupLine.setAdapter(new StrokeWidthListAdapter(getActivity(), vfd.getmPaint()));
                    }
                }
            }
        });
        strokeStyleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupStyle == null){
                    listPopupStyle = new ListPopupWindow(getActivity());
                    listPopupStyle.setAdapter(new StrokeFillTypeListAdapter(getActivity(), vfd.getmPaint()));
                    listPopupStyle.setAnchorView(strokeStyleIB);
                    listPopupStyle.setPromptPosition(ListPopupWindow.POSITION_PROMPT_ABOVE);
                    listPopupStyle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Paint.Style style = (Paint.Style) parent.getItemAtPosition(position);
                            vfd.getmPaint().setStyle(style);
                            listPopupStyle.dismiss();
                        }
                    });
                    listPopupStyle.show();
                }else{
                    if (listPopupStyle.isShowing()){
                        listPopupStyle.dismiss();
                    }else{
                        listPopupStyle.show();
                        listPopupStyle.setAdapter(new StrokeFillTypeListAdapter(getActivity(), vfd.getmPaint()));
                    }
                }
                freeHandToggleB.setChecked(true);
            }
        });
        saveIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vfd.finallyDraw();
                EditorActivity activity = (EditorActivity) getActivity();
                if (activity.saveCurrentPhoto(activity.getWafImage())){
                    Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
        toggleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ToggleButton toggleButton = (ToggleButton) group.getChildAt(i);
                    toggleButton.setChecked(toggleButton.getId() == checkedId);
                    vfd.setFreeHand(freeHandToggleB.isChecked());
                    vfd.setFloodFill(oneLineToggleB.isChecked());
                }
            }
        });
        grayScaleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorMatrixColorFilter colorMatrixColorFilter = vfd.grayscale();
                vfd.getmBitmapPaint().setColorFilter(colorMatrixColorFilter);
            }
        });
        openIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActionBarBottom();
            }
        });

        textureIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTexture();
            }
        });

    }


    public void openActionBarBottom(){

        if (bottomActionBar.getHeight() <= getResources().getDisplayMetrics().heightPixels/10-1){ // chiuso/aperto
            bottomActionBar.getLayoutParams().height = getResources().getDisplayMetrics().heightPixels/10;
            bottomActionBar.requestLayout();
            for (View v : listButton){
                v.setVisibility(View.VISIBLE);
            }

            Log.e(LOG_EDITFRAG,"chiuso/aperto");
        }else{                                // aperto/chiuso
            bottomActionBar.getLayoutParams().height = getResources().getDisplayMetrics().heightPixels/30;
            bottomActionBar.requestLayout();
            for (View v : listButton){
                v.setVisibility(View.GONE);
            }
            Log.e(LOG_EDITFRAG,"aperto/chiuso");
        }
    }
    private void dialogTexture(){
        Context context = getActivity();
        Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_texture,null);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        ColorGridAdapter adapter = new ColorGridAdapter(context,App.getDrawablesDefault());
        gridView.setAdapter(adapter);
        dialog.setContentView(view);
        dialog.show();
    }

    public ViewForDrawIn getVfd() {
        return vfd;
    }

    public void setVfd(ViewForDrawIn vfd) {
        this.vfd = vfd;
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

    public WafImage getWafImage() {
        return wafImage;
    }

    public void setWafImage(WafImage wafImage) {
        this.wafImage = wafImage;
    }
}
