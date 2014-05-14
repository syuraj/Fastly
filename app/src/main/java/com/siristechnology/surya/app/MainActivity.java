package com.siristechnology.surya.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements TextWatcher,
        FetchInstalledAppsTask.FetchInstalledAppsTaskListener,
        FetchRecentsAppsTask.FetchRecentsAppsTaskListener,
        LoadIconsTask.LoadIconsTaskListener {

    private List<AppInfo> apps;
    List<AppInfo> filteredApps;
    protected GridView aGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;

        setupGridView();

        setupSearchEditText();

        initiateLoadIconsTask();

        initiateInstalledAppsFetch();
    }

    private void initiateLoadIconsTask() {
        LoadIconsTask iconsTask = new LoadIconsTask(this, this.apps);
        iconsTask.execute(1);
    }

    public void handleAppIconsLoaded(List<AppInfo> apps) {
        this.apps = apps;
    }

    private void initiateInstalledAppsFetch() {
        FetchInstalledAppsTask fetchTask = new FetchInstalledAppsTask(this);
        fetchTask.execute(1);
    }

    public void handleInstalledAppsFetch(List<AppInfo> apps) {
        if (this.apps.size() == 0) {
            this.apps = apps;
            search("");
        }

        initiateRecentAppsFetch();
    }

    private void initiateRecentAppsFetch() {
        FetchRecentsAppsTask fetchTask = new FetchRecentsAppsTask(this);
        fetchTask.execute(1);
    }

    public void handleRecentAppsFetch() {

    }

    public void handleAppIconsLoaded() {
    }

    @Override
    public void afterTextChanged(Editable s) {
        aGridView.invalidateViews();
        search(s.toString());
    }

    private void setupGridView() {
        aGridView = (GridView) this.findViewById(R.id.app_gridview);

        aGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                AppInfo selectedApp = filteredApps.get(position);
                selectedApp.launch(getApplicationContext());
            }
        });

        populateGridViewFromDatabase();
    }

    private void populateGridViewFromDatabase() {
        AppsDatabaseHandler db = new AppsDatabaseHandler(this);
        this.apps = db.getAllApps(this);

        if (this.apps.size() > 0) {
            search("");
        }
    }

    private void setupSearchEditText() {
        SearchEditText search_field = (SearchEditText) findViewById(R.id.search_field);
        search_field.addTextChangedListener(this);

        search_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    v.setText("");
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void search(String searchTerm) {
        if (apps != null) {
            filteredApps = new ArrayList<AppInfo>();

            int x = 0;
            for (AppInfo app : apps) {
                if (app.name.toLowerCase().contains(searchTerm.toLowerCase())) {
                    if (x++ < 12) {
                        filteredApps.add(app);
                    } else {
                        break;
                    }
                }
            }

            Collections.sort(filteredApps, new AppInfoCompare());

            GridArrayAdapter gAdapter = new GridArrayAdapter(this, filteredApps);
            aGridView.setAdapter(gAdapter);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(java.lang.CharSequence charSequence, int start, int before, int count) {
    }


}
