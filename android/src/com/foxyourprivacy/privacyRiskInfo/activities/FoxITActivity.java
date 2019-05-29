package com.foxyourprivacy.privacyRiskInfo.activities;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.foxyourprivacy.privacyRiskInfo.PRIApplication;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;
import com.foxyourprivacy.privacyRiskInfo.asynctasks.CSVUpdateTask;

import java.util.Calendar;

/**
 * device and user specific content is retrieved and saved in database
 * including installed apps, respective permissions & settings.
 */
public abstract class FoxITActivity extends AppCompatActivity {


    @Override
    public void onStart() { //TODO Activity which every Activity inherits does way too much in onStart!
        super.onStart();

        ValueKeeper v = ValueKeeper.getInstance();
        PRIApplication myApp = (PRIApplication) this.getApplication();
        if (v.getFreshlyStarted()) {

            v.reviveInstance(getApplicationContext());

            //  new reviveValueTask().execute();
            v.setTimeOfFirstAccess(System.currentTimeMillis());
        }


        if (myApp.wasInBackground || v.getFreshlyStarted()) {
            Log.d("Privacy Risk Info", "Was in Background");
            v.setFreshlyStarted(false);
            v.setTimeOfLastAccess(Calendar.getInstance().getTimeInMillis());
        }

        if (v.getTimeOfLastServerAccess() + 259200000 < Calendar.getInstance().getTimeInMillis() && v.getTimeOfLastServerAccess() != 0L) {
            NetworkInfo netInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/lektionen.csv", "lessons");
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/classes.csv", "classes");
                v.setTimeOfThisServerAccess();
                Log.d("FoxITActivity", "started an update of CSV files from server");
            } else {
                //retry in one day
                v.setTimeOfNextServerAccess(System.currentTimeMillis() + 86400000);
                Log.d("FoxITActivity", "delayed an update of CSV files from server");

            }


        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ValueKeeper v = ValueKeeper.getInstance();
        v.fillApplicationAccessAndDuration(System.currentTimeMillis());
        v.fillApplicationStartAndDuration(System.currentTimeMillis());
        v.fillApplicationStartAndActiveCDuration(System.currentTimeMillis());
        ((PRIApplication) this.getApplication()).startActivityTransitionTimer();
        if (this instanceof LessonActivity || this instanceof LessonListActivity)
            v.saveInstance(getApplicationContext());
    }


}
