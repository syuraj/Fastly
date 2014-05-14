package com.siristechnology.surya.app;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Arrays;
import java.util.List;


public class LoadIconsTask extends AsyncTask<Integer, Void, List<AppInfo>> {
    private Context context;
    private LoadIconsTaskListener listener;
    List<AppInfo> apps;

    public LoadIconsTask(Context context, List<AppInfo> apps) {
        this.context = context;
        this.listener = (LoadIconsTaskListener) context;
        this.apps = apps;
    }

    protected List<AppInfo> doInBackground(Integer... params) {
        for (AppInfo app : apps) {
            try {
                app.icon = context.getPackageManager().getApplicationIcon(app.packageName);
            } catch (Exception e) {
            }
        }

        return apps;
    }

    protected void onPostExecute(List<AppInfo> apps) {
        listener.handleAppIconsLoaded(apps);
    }

    interface LoadIconsTaskListener {
        void handleAppIconsLoaded(List<AppInfo> apps);
    }
}
