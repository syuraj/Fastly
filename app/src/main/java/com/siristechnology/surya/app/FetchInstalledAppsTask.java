package com.siristechnology.surya.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;


public class FetchInstalledAppsTask extends AsyncTask<Integer, Void, List<AppInfo>> {
    private Context context;
    private FetchInstalledAppsTaskListener listener;
    AppsDatabaseHandler appsDbHandler;

    public FetchInstalledAppsTask(Context context) {
        this.context = context;
        this.listener = (FetchInstalledAppsTaskListener) context;
        this.appsDbHandler = new AppsDatabaseHandler(context);
    }

    protected List<AppInfo> doInBackground(Integer... params) {
        List<AppInfo> apps = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo ri : pkgAppsList) {
            if (ri.activityInfo != null) {
                AppInfo app = new AppInfo();

                app.name = ri.activityInfo.loadLabel(pm).toString();
                app.packageName = ri.activityInfo.packageName;
                app.icon = ri.activityInfo.loadIcon(pm);
                app.recentlyused = false;
                app.timesUsed = 0;

                apps.add(app);
                appsDbHandler.addIfNotExists(app);
            }
        }

        return apps;
    }

    protected void onPostExecute(List<AppInfo> apps) {
        listener.handleInstalledAppsFetch(apps);
    }

    interface FetchInstalledAppsTaskListener {
        void handleInstalledAppsFetch(List<AppInfo> apps);
    }
}
