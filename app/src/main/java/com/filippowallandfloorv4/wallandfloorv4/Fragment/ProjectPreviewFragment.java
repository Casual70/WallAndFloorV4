package com.filippowallandfloorv4.wallandfloorv4.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class ProjectPreviewFragment extends Fragment {

    public GridView projectPreview;

    public static ProjectPreviewFragment newIstance(GridView projectPreview){
        ProjectPreviewFragment fragment = new ProjectPreviewFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    public ProjectPreviewFragment(){}

    public void setProjectPreview(GridView projectPreview) {
        this.projectPreview = projectPreview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return projectPreview;
    }
}
