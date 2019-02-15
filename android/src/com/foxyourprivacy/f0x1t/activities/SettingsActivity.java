package com.foxyourprivacy.f0x1t.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.foxyourprivacy.f0x1t.R;
import com.foxyourprivacy.f0x1t.asynctasks.CSVUpdateTask;
import com.foxyourprivacy.f0x1t.asynctasks.DBWrite;
import com.foxyourprivacy.f0x1t.fragments.SettingsFragment;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SettingsActivity extends FoxITActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Fragment is created
        SettingsFragment fragment = new SettingsFragment();

        //add fragment so the activitys' context
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.first_fragment_frame, fragment, "permissionDescription");
        transaction.commit();

    }

    private ArrayList readCSV(int input, Context context) {
        InputStream is = context.getResources().openRawResource(input);
        ArrayList result = new ArrayList();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String templine;
            while ((templine = br.readLine()) != null) {
                String[] csvrow = templine.split(";");
                result.add(csvrow);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV file couldn't be read properly: " + e);
        } finally {
            cleanup(is);
            cleanup(br);
        }
        return result;

    }

    private ArrayList readLessonCSV(int input, Context context) {
        InputStream is = context.getResources().openRawResource(input);
        ArrayList result = new ArrayList();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String templine;
            StringBuilder sb = new StringBuilder();
            while ((templine = br.readLine()) != null) {
                sb.append(templine);
                if (templine.matches(".*;;;")) {
                    String[] rowarray = sb.toString().split(";");
                    result.add(rowarray);
                    // Log.d("SettingsActivity", "row: " + csvrow);
                    sb.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV file couldn't be read properly: " + e);
        } finally {
            cleanup(is);
            cleanup(br);
        }
        return result;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.analyze).setVisible(false);
        menu.findItem(R.id.action_options).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updatePermissions(Context context, ConnectivityManager connMan) {
        //checking connectivity to a network
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //update from internet resource
            String URL = "https://foxit.secuso.org/CSVs/raw/permissions.csv";//"https://app.seafile.de/f/740b44b607/?raw=1";
            new CSVUpdateTask().execute(context, URL, "permissions", readCSV(R.raw.permissions, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            new DBWrite().execute(context, "updatePermissions", readCSV(R.raw.permissions, context));
        }

    }

    public void updateLessions(Context context, ConnectivityManager connMan) {
        //checking connectivity to a network
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //update from internet resource
            String URL = "https://foxit.secuso.org/CSVs/raw/lektionen.csv";//"https://app.seafile.de/f/e27034ec0a/?raw=1";
            new CSVUpdateTask().execute(context, URL, "lessons", readLessonCSV(R.raw.lektionen, context));
            URL = "https://foxit.secuso.org/CSVs/raw/classes.csv";//"https://app.seafile.de/f/7ca81fac4e/?raw=1";
            new CSVUpdateTask().execute(context, URL, "classes", readCSV(R.raw.classes, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            //DBHandler dbHandler = new DBHandler(context, null, null, 1);
            new DBWrite().execute(context, "updateLessons", readLessonCSV(R.raw.lektionen, context));
            //dbHandler.updateLessons(readCSV(R.raw.lektionen, context));
            new DBWrite().execute(context, "updateClasses", readCSV(R.raw.classes, context));
            //dbHandler.updateClasses(readCSV(R.raw.classes, context));
            //dbHandler.close();
        }
    }

    public void updateSettings(Context context, ConnectivityManager connMan) {
        //checking connectivity to a network
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //update from internet resource
            String URL = "https://foxit.secuso.org/CSVs/raw/sdescription.csv";//"https://app.seafile.de/f/bb0071411b/?raw=1";
            new CSVUpdateTask().execute(context, URL, "settings", readCSV(R.raw.sdescription, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            new DBWrite().execute(context, "updateSettingDescriptions", readCSV(R.raw.sdescription, context));
        }
    }


    /**
     * overrides the behavior of the backButton for it to properly support Fragments and Fragments in Fragments (ChildFragments)
     * @author Tim
     */
    @Override
    public void onBackPressed() {
        //if there is a fragment
        RelativeLayout firstFragmentFrame = findViewById(R.id.first_fragment_frame);


        if (firstFragmentFrame.getVisibility() == View.GONE) {
            getFragmentManager().popBackStack(); //destroy PermissionListFragment
            firstFragmentFrame.setVisibility(View.VISIBLE); //make the hidden appList visible again

        } else {//if no fragments exist behave normal
            super.onBackPressed();
        }
    }

    private void cleanup(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            Log.d("SettingsActivity", "Input Stream couldn't be closed properly");
            e.printStackTrace();
        }

    }


}
