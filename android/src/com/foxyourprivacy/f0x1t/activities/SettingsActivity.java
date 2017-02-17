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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class SettingsActivity extends FoxITActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Fragment is created
        SettingsFragment fragment = new SettingsFragment();

        //add fragment so the activitys' context
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.first_fragment_frame, fragment, "permissionDescription");
        transaction.commit();

    }

    public ArrayList readCSV(int input, Context context) {
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
            try {
                is.close();
                br.close();
            } catch (IOException e) {
                throw new RuntimeException("Input Stream couldn't be closed properly: " + e);
            }
        }
        return result;

    }

    public ArrayList readLessionCSV(int input, Context context) {
        InputStream is = context.getResources().openRawResource(input);
        ArrayList result = new ArrayList();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String templine;
            String csvrow = "";
            while ((templine = br.readLine()) != null) {
                csvrow += templine;
                if (templine.matches(".*;;;")) {
                    String[] rowarray = csvrow.split(";");
                    result.add(rowarray);
                    // Log.d("SettingsActivity", "row: " + csvrow);
                    csvrow = "";
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV file couldn't be read properly: " + e);
        } finally {
            try {
                is.close();
                br.close();
            } catch (IOException e) {
                throw new RuntimeException("Input Stream couldn't be closed properly: " + e);
            }
        }
        return result;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.goOn).setVisible(false);
        menu.findItem(R.id.goBack).setVisible(true);
        menu.findItem(R.id.action_options).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.goBack) {
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
            new CSVUpdateTask(context).execute(URL, "permissions", readCSV(R.raw.permissions, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            new DBWrite(context).execute("updatePermissions", readCSV(R.raw.permissions, context));
        }

    }

    public void updateLessions(Context context, ConnectivityManager connMan) {
        //checking connectivity to a network
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //update from internet resource
            String URL = "https://foxit.secuso.org/CSVs/raw/lektionen.csv";//"https://app.seafile.de/f/e27034ec0a/?raw=1";
            new CSVUpdateTask(context).execute(URL, "lessions", readLessionCSV(R.raw.lektionen, context));
            URL = "https://foxit.secuso.org/CSVs/raw/classes.csv";//"https://app.seafile.de/f/7ca81fac4e/?raw=1";
            new CSVUpdateTask(context).execute(URL, "classes", readCSV(R.raw.classes, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            //DBHandler dbHandler = new DBHandler(context, null, null, 1);
            new DBWrite(context).execute("updateLessons", readLessionCSV(R.raw.lektionen, context));
            //dbHandler.updateLessons(readCSV(R.raw.lektionen, context));
            new DBWrite(context).execute("updateClasses", readCSV(R.raw.classes, context));
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
            new CSVUpdateTask(context).execute(URL, "settings", readCSV(R.raw.sdescription, context));

        } else {
            //fallback on local data provided by apk
            Log.d("SettingsActivity: ", "no internet connection");
            new DBWrite(context).execute("updateSettingDescriptions", readCSV(R.raw.sdescription, context));
        }
    }


    @Override
    /**
     * overrides the behavior of the backButton for it to properly support Fragments and Fragments in Fragments (ChildFragments)
     * @author Tim
     */

    public void onBackPressed() {
        //if there is an fragment
        RelativeLayout firstFragmentFrame = (RelativeLayout) findViewById(R.id.first_fragment_frame);


        if (firstFragmentFrame.getVisibility() == View.GONE) {
            getFragmentManager().popBackStack(); //destroy PermissionListFragment
            firstFragmentFrame.setVisibility(View.VISIBLE); //make the hidden appList visible again

        } else {//if no fragments exist behave normal
            super.onBackPressed();
        }
    }


}