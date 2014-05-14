package com.siristechnology.surya.app;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

public class AppInfo {
    public Integer id;
    public String name;
    public Drawable icon;
    public String packageName;
    public boolean recentlyused;
    public Integer timesUsed;

    public void launch(Context context) {
        try {
            try {
                AppsDatabaseHandler db = new AppsDatabaseHandler(context);
                db.updateAppLaunch(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(launchIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Application launch failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
