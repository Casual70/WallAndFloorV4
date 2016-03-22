package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.filippowallandfloorv4.wallandfloorv4.Activity.EditorActivity;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.DialogTextureCustomGridAdapter;
import com.filippowallandfloorv4.wallandfloorv4.Adapter.DialogTextureGridAdapter;
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

    private static final String LOG_EDITFRAG = "EditorFragment";
    private static final String KEY_WAFIMAGE = "save_waf";

    public ViewForDrawIn vfd;
    public RelativeLayout bottomActionBar;
    public RelativeLayout relativeLayout_editor;
    public ImageButton openIB, strokeWidthIB, strokeStyleIB, grayScaleIB,textureIB,saveIB;
    public ToggleButton freeHandToggleB, oneLineToggleB;
    public ListPopupWindow listPopupStyle, listPopupLine;
    public RadioGroup toggleGroup;
    public Bitmap mBitmap;
    public Bitmap mTextureBitmap;
    public Paint mPaint;
    public View[]listButton;
    private WafImage wafImage;

    public EditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(LOG_EDITFRAG, "OncreateView");
        View view = inflater.inflate(R.layout.prova2,null);
        vfd = (ViewForDrawIn)view.findViewById(R.id.ViewForDrawIn);
        if (savedInstanceState != null){

        }
        vfd.setmBitmap(mBitmap);
        vfd.setmPaint(mPaint);
        vfd.setWafImage(wafImage);
        vfd.setEditorFragment(this);

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
        relativeLayout_editor = (RelativeLayout)view.findViewById(R.id.fragment_layout_editor);

        return view;
    }

    @Override
    public void onClick(View v) {
        boolean check = ((RadioGroup)v.getParent()).getCheckedRadioButtonId() == v.getId();
        if (check){
            ((RadioGroup)v.getParent()).clearCheck();
        }else{
            ((RadioGroup)v.getParent()).check(v.getId());
        }
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
                Log.e("call","listenercall");
                for (int i = 0; i < group.getChildCount(); i++) {
                    ToggleButton toggleButton = (ToggleButton) group.getChildAt(i);
                    toggleButton.setChecked(toggleButton.getId() == checkedId);
                    vfd.setFreeHand(freeHandToggleB.isChecked());
                    vfd.setFloodFill(oneLineToggleB.isChecked());
                    Log.e(LOG_EDITFRAG,"freehand: "+freeHandToggleB.isChecked() + " -- floodfill: "+oneLineToggleB.isChecked());
                }
            }
        });
        grayScaleIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ColorMatrixColorFilter colorMatrixColorFilter = vfd.grayscale();
                //vfd.getmBitmapPaint().setColorFilter(colorMatrixColorFilter);
                vfd.CannyBord();
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
        final Context context = getActivity();
        final Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_texture,null);
        final GridView gridView = (GridView) view.findViewById(R.id.gridView);
        final TextView defautlText = (TextView)view.findViewById(R.id.defaultTexture);
        final TextView customText = (TextView)view.findViewById(R.id.customTexture);
        final ImageView addtexture = (ImageView)view.findViewById(R.id.addTexture);
        final DialogTextureGridAdapter adapterDef = new DialogTextureGridAdapter(context,App.getDrawablesDefault());
        final DialogTextureCustomGridAdapter adapterCust = new DialogTextureCustomGridAdapter(context,App.getAppIstance().getImageDb().getAllWafImageTexture(),true);
        gridView.setAdapter(adapterCust);
        dialog.setContentView(view);
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == defautlText.getId()){
                    gridView.setAdapter(adapterDef);
                }
                else if (v.getId() == customText.getId()){
                    gridView.setAdapter(adapterCust);
                }
            }
        };
        defautlText.setOnClickListener(click);
        customText.setOnClickListener(click);
        addtexture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // da photo
                // prendere esempio da qui
                //com.filippowallandfloorv4.wallandfloorv4.Activity.MainActivity#takeNewPhoto

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer item = adapterDef.getItem(position);
                mTextureBitmap = BitmapFactory.decodeResource(getResources(),item);
                if (mTextureBitmap == null){
                    Toast.makeText(context,"decode Resorce Fail",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                BitmapShader shader = new BitmapShader(mTextureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                vfd.getmPaint().setShader(shader);
                vfd.getmPaint().setAlpha(250);
                dialog.dismiss();
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_WAFIMAGE,wafImage);
        Log.e(LOG_EDITFRAG,"onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e(LOG_EDITFRAG, "onViewStateRestored");

    }


}
