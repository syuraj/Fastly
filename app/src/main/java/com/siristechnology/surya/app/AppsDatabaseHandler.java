package com.siristechnology.surya.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class AppsDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppsDB";
    private static final String APPS_TABLE = "AppsTable";

    private static final String COL_ID = "ID";
    private static final String COL_PACKAGE_NAME = "PACKAGE_NAME";
    private static final String COL_APP_NAME = "APP_NAME";
    private static final String COL_RECENTLY_USED = "RECENTLY_USED";
    private static final String COL_TIMES_USED = "TIMES_USED";

    public AppsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_APPS_TABLE = "CREATE TABLE " + APPS_TABLE + "("
                + COL_ID + " INTEGER PRIMARY KEY,"
                + COL_PACKAGE_NAME + " TEXT UNIQUE,"
                + COL_APP_NAME + " TEXT,"
                + COL_RECENTLY_USED + " INTEGER,"
                + COL_TIMES_USED + " INTEGER"
                + ")";

        db.execSQL(CREATE_APPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + APPS_TABLE);
        onCreate(db);
    }

    public void addIfNotExists(AppInfo app) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT OR IGNORE INTO " + APPS_TABLE + " (" + COL_PACKAGE_NAME + "," + COL_APP_NAME + "," + COL_RECENTLY_USED + "," + COL_TIMES_USED + ")"
                + " VALUES('" + app.packageName + "','" + app.name + "', " + (app.recentlyused ? 1 : 0) + ", " + app.timesUsed + ")";

        db.execSQL(query);
    }

    public void updateRecentAppsList(List<AppInfo> apps) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query_reset_recent = "UPDATE " + APPS_TABLE + " SET " + COL_RECENTLY_USED + " =0";
        db.execSQL(query_reset_recent);

        StringBuilder packageNames = new StringBuilder();
        for (AppInfo app : apps) {
            packageNames.append("'" + app.packageName + "',");
        }

        String query = "Update " + APPS_TABLE + " SET " + COL_RECENTLY_USED + " = 1, " + COL_TIMES_USED + " =" + COL_TIMES_USED + " + 1"
                + " WHERE " + COL_PACKAGE_NAME + " IN (" + packageNames.substring(0, packageNames.length() - 1) + ")";

        db.execSQL(query);
    }

    public void updateAppLaunch(AppInfo app) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Update " + APPS_TABLE + " SET " + COL_RECENTLY_USED + " = 1, " + COL_TIMES_USED + " =" + COL_TIMES_USED + " + 1"
                + " WHERE " + COL_PACKAGE_NAME + " ='" + app.packageName + "'";

        db.execSQL(query);
    }

    public List<AppInfo> getAllApps(Context context) {
        List<AppInfo> apps = new ArrayList<AppInfo>();

        String selectQuery = "SELECT  * FROM " + APPS_TABLE;
        selectQuery += " ORDER BY " + COL_RECENTLY_USED + " DESC, " + COL_TIMES_USED + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int i = 0;
            int gridSize = Integer.parseInt(context.getString(R.integer.grid_apps_count));

            do {
                AppInfo app = new AppInfo();
                app.name = cursor.getString(2);
                app.packageName = cursor.getString(1);

                //load icons for only first screen
                if (i < gridSize) {
                    try {
                        app.icon = context.getPackageManager().getApplicationIcon(app.packageName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }

                app.recentlyused = cursor.getInt(3) > 0;
                app.timesUsed = cursor.getInt(4);

                apps.add(app);
            } while (cursor.moveToNext());
        }

        return apps;
    }

}
