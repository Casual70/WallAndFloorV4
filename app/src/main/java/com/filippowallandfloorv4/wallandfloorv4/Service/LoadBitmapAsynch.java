package com.filippowallandfloorv4.wallandfloorv4.Service;

import android.os.AsyncTask;

import com.filippowallandfloorv4.wallandfloorv4.Adapter.GridPreviewCursorAdapter;

/**
 * Created by Filippo on 25/01/2016.
 */
public class LoadBitmapAsynch extends AsyncTask {

    private GridPreviewCursorAdapter mAdapterCursor;

    public LoadBitmapAsynch(GridPreviewCursorAdapter mAdapterCursor) {
        this.mAdapterCursor = mAdapterCursor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
