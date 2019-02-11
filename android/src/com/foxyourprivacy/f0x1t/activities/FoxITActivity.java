package com.foxyourprivacy.f0x1t.activities;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.foxyourprivacy.f0x1t.FoxITApplication;
import com.foxyourprivacy.f0x1t.R;
import com.foxyourprivacy.f0x1t.ValueKeeper;
import com.foxyourprivacy.f0x1t.asynctasks.CSVUpdateTask;
import com.foxyourprivacy.f0x1t.fragments.TrophyNotificationFragment;

import java.util.Calendar;

/**
 * device and user specific content is retrieved and saved in database
 * including installed apps, respective permissions & settings.
 */
public abstract class FoxITActivity extends AppCompatActivity {




    /**
     * @author Tim
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ValueKeeper v = ValueKeeper.getInstance();
        if (v.getSizeOfAppStarts() > 4) {
            setTrophyUnlocked("Power User");
        }
    }

    @Override
    public void onStart() { //TODO Activity which every Activity inherits does way too much in onStart!
        super.onStart();

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) Log.d("NotificationProblem", "NotificationManager is null");
        else nm.cancelAll();

        ValueKeeper v = ValueKeeper.getInstance();
        FoxITApplication myApp = (FoxITApplication) this.getApplication();
        if (v.getFreshlyStarted()) {

            v.reviveInstance(getApplicationContext());

            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY) + 2;


            if (timeOfDay >= 5 && timeOfDay < 9) {
                v.increaseNumberMorning();
                if (v.getNumberOfTimesOpenedAtMorning() > 4) {
                    setTrophyUnlocked("Early Bird");
                }


            } else {
                if (timeOfDay >= 16 || timeOfDay < 5) {

                    v.increaseNumberNight();
                    if (v.getNumberOfTimesOpenedAtNight() > 4) {
                        setTrophyUnlocked("Nachteule");
                    }
                }


            }


            //  new reviveValueTask().execute();
            v.addAppStarts(System.currentTimeMillis());
            v.setTimeOfFirstAccess(System.currentTimeMillis());


        }


        if (myApp.wasInBackground || v.getFreshlyStarted()) {
            Log.d("MyApp", "Was in Background");
            v.setFreshlyStarted(false);
            v.setTimeOfLastAccess(Calendar.getInstance().getTimeInMillis());
        }

        if (v.getTimeOfLastServerAccess() + 259200000 < Calendar.getInstance().getTimeInMillis() && v.getTimeOfLastServerAccess() != 0L) {
            NetworkInfo netInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/permissions.csv", "permissions");
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/testcsv.csv", "lessons");
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/classes.csv", "classes");
                new CSVUpdateTask().execute(this, "https://foxit.secuso.org/CSVs/raw/sdescription.csv", "settings");
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
        ((FoxITApplication) this.getApplication()).startActivityTransitionTimer();
        if (this instanceof LessonActivity || this instanceof LessonListActivity || this instanceof TrophyRoomActivity || this instanceof Analysis || this instanceof Home)
            v.saveInstance(getApplicationContext());
    }



    public boolean setTrophyUnlocked(String trophyName) {
        ValueKeeper v = ValueKeeper.getInstance();
        if (!(v.trophyList.containsKey(trophyName) && v.trophyList.get(trophyName))) {
            displayTrophyUnlocked(trophyName);
        }
        if (v.trophyList.containsKey(trophyName)) {
            v.trophyList.put(trophyName, true);
            return true;
        } else {
            v.trophyList.put(trophyName, true);
            return false;
        }

    }


    private void displayTrophyUnlocked(String trophyName) {

        //add the acornCountFragment to the activity's context
        final FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        final TrophyNotificationFragment trophyNotification = new TrophyNotificationFragment();
        Bundle name = new Bundle();
        name.putString("name", trophyName);
        trophyNotification.setArguments(name);
        //add the fragment to the count_frame RelativeLayout
        if (this.findViewById(R.id.count_frame) != null) {
            transaction.add(R.id.count_frame, trophyNotification, "trophy");
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }
    }

}
