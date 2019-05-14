package com.foxyourprivacy.privacyRiskInfo;

import android.app.Application;

import java.util.Timer;
import java.util.TimerTask;

/**
 * a parent class to many of the activities of FoxIT.
 * things that have to be done on every activity call are done here (unfortunately, some more too)
 * Created by Ich on 18.10.2016.
 */


//TODO extract functions that are not important for every activity!
public class PRIApplication extends Application {


    public boolean wasInBackground;
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;

    public void startActivityTransitionTimer() {
        final long MAX_ACTIVITY_TRANSITION_TIME_MS = 10000;
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                PRIApplication.this.wasInBackground = true;
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
    }

}

