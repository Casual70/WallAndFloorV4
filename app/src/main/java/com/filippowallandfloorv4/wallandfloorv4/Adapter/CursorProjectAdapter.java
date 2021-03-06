package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

/**
 * Created by Filippo on 16/10/2015.
 */
public class CursorProjectAdapter extends CursorTreeAdapter {

    private Cursor cursor;
    private ProjectlistCursorItemHolder holder;
    private LayoutInflater inflater;
    private String projectName;

    public CursorProjectAdapter(Cursor c, Context context, boolean autoRequery) {
        super(c, context, autoRequery);
        this.cursor = c;
        this.inflater = LayoutInflater.from(context);
    }

    public String getProjectName(int position) {
        String nameProject = null;
        if (cursor.moveToPosition(position)){
            nameProject = cursor.getString(cursor.getColumnIndex(ImageDb.NAME_PROJECT));
        }
        return nameProject;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return App.getAppIstance().getImageDb().getAllZoneByProjectCursor(groupCursor.getString(groupCursor.getColumnIndex(ImageDb.NAME_PROJECT)));
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.projectlist_cursor_item_group,parent,false);
        holder = new ProjectlistCursorItemHolder(retView);
        retView.setTag(holder);
        return retView;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        ProjectlistCursorItemHolder holder = (ProjectlistCursorItemHolder) view.getTag();
        String nameProject = cursor.getString(cursor.getColumnIndex(ImageDb.NAME_PROJECT));

        holder.getProjectNameLabelOnlist().setText(nameProject);
        Cursor size = App.getAppIstance().getImageDb().getAllWafImageSortByProjectCursor(nameProject);
        holder.getProjectNameImagecountOnlist().setText(String.valueOf(size.getCount()));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View childView = inflater.inflate(R.layout.projectlist_cursor_item_group,null);
        holder = new ProjectlistCursorItemHolder(childView);
        childView.setTag(holder);
        return childView;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        ProjectlistCursorItemHolder holder = (ProjectlistCursorItemHolder) view.getTag();
        String nameZone = cursor.getString(cursor.getColumnIndex(ImageDb.NAME_ZONE));
        holder.getProjectNameLabelOnlist().setText(nameZone);
        holder.getProjectNameLabelOnlist().setTextSize(20);
        holder.getProjectNameLabelOnlist().setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        //Cursor size = App.getAppIstance().getImageDb().getAllWafImageSortByProjectCursor(nameZone);
        //holder.getProjectNameImagecountOnlist().setText(String.valueOf(size.getCount()));

        holder.getProjectNameImagecountOnlist().setVisibility(View.GONE);
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }





}
