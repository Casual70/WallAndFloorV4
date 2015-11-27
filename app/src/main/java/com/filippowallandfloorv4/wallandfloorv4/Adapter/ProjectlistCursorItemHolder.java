package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.view.View;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.R;

public class ProjectlistCursorItemHolder {
    private TextView ProjectNameLabelOnlist;
    private TextView ProjectNameImagecountOnlist;

    public ProjectlistCursorItemHolder(View view) {
        ProjectNameLabelOnlist = (TextView) view.findViewById(R.id.ProjectName_label_onlist);
        ProjectNameImagecountOnlist = (TextView) view.findViewById(R.id.ProjectName_imagecount_onlist);
    }

    public TextView getProjectNameImagecountOnlist() {
        return ProjectNameImagecountOnlist;
    }

    public TextView getProjectNameLabelOnlist() {
        return ProjectNameLabelOnlist;
    }
}
