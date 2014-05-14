package com.siristechnology.surya.app;

import java.util.Comparator;


public class AppInfoCompare implements Comparator<AppInfo> {
    @Override
    public int compare(AppInfo app1, AppInfo app2) {
        return app1.timesUsed.compareTo(app2.timesUsed);
    }
}
