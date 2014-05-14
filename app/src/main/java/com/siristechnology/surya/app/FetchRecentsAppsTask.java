package com.siristechnology.surya.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FetchRecentsAppsTask extends AsyncTask<Integer, Void, Void> {
    private Context context;
    private FetchRecentsAppsTaskListener listener;
    AppsDatabaseHandler appsDbHandler;

    public FetchRecentsAppsTask(Context context) {
        this.context = context;
        this.listener = (FetchRecentsAppsTaskListener) context;
        this.appsDbHandler = new AppsDatabaseHandler(context);
    }

    protected Void doInBackground(Integer... params) {
        List<AppInfo> apps = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(Integer.MAX_VALUE, 0);

        for (ActivityManager.RecentTaskInfo ri : recentTasks) {
            Intent intent = ri.baseIntent;

            if (intent.getAction() == Intent.ACTION_MAIN
                    && intent.getCategories() != null
                    && intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
                AppInfo app = new AppInfo();
                app.packageName = intent.getComponent().getPackageName();

                apps.add(app);
            }
        }

        appsDbHandler.updateRecentAppsList(apps);

        return null;
    }

    @Override
    protected void onPostExecute(Void t) {
        listener.handleRecentAppsFetch();
    }

    interface FetchRecentsAppsTaskListener {
        void handleRecentAppsFetch();
    }
}
