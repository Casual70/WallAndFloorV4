package com.filippowallandfloorv4.wallandfloorv4.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.filippowallandfloorv4.wallandfloorv4.App;
import com.filippowallandfloorv4.wallandfloorv4.R;
import com.filippowallandfloorv4.wallandfloorv4.SqlDb.ImageDb;

/**
 * Created by Filippo on 16/10/2015.
 */
public class CursorProjectAdapter extends CursorAdapter {

    private Cursor cursor;
    private ProjectlistCursorItemHolder holder;
    private LayoutInflater inflater;

    public CursorProjectAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.cursor = c;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View retView = inflater.inflate(R.layout.projectlist_cursor_item,parent,false);
        holder = new ProjectlistCursorItemHolder(retView);
        retView.setTag(holder);
        return retView;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ProjectlistCursorItemHolder holder = (ProjectlistCursorItemHolder) view.getTag();
        String nameProject = cursor.getString(cursor.getColumnIndex(ImageDb.NAME_PROJECT));

        holder.getProjectNameLabelOnlist().setText(nameProject);
        Cursor size = App.getAppIstance().getImageDb().getAllWafImageSortByProjectCursor(nameProject);
        holder.getProjectNameImagecountOnlist().setText(String.valueOf(size.getCount()));
    }

    @Override
    public String getItem(int position) {
        Cursor cursor = getCursor();
        String nameProject = null;
        if (cursor.moveToPosition(position)){
            nameProject = cursor.getString(cursor.getColumnIndex(ImageDb.NAME_PROJECT));
        }
        return nameProject;
    }
}
