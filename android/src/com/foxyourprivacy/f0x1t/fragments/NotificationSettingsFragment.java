package com.foxyourprivacy.f0x1t.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.foxyourprivacy.f0x1t.R;
import com.foxyourprivacy.f0x1t.ValueKeeper;
import com.foxyourprivacy.f0x1t.unlockWorker;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * This is a Fragment for the settings screen, where your classes and descriptions for permissions and settings can be updated
 * Created by Tim on 25.06.2016.
 */

public class NotificationSettingsFragment extends Fragment {


    /**
     * fills the layout with the permission name and description
     *
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        Button unlockButton = view.findViewById(R.id.button_switch_notifications);
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int targetDelay = (((17 - hour) % 24) + 24) % 24;
                int minute = Calendar.getInstance().get(Calendar.MINUTE);
                int totalDelay = targetDelay * 3600000 - minute * 60000;
                Timer timer = new Timer();
                timer.schedule(new timedStart(), totalDelay);
            }
        });

        RadioGroup radio = view.findViewById(R.id.notification_selection);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ValueKeeper v = ValueKeeper.getInstance();
                if (checkedId == R.id.notifications_on) {
                    v.setNotificationsWanted(true);
                } else {
                    v.setNotificationsWanted(false);
                }

            }
        });


        return view;
    }

    /**
     * Enables to pass arguments to the fragment
     *
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {

    }

    class timedStart extends TimerTask {

        @Override
        public void run() {
            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(unlockWorker.class, 1, DAYS, 3, TimeUnit.HOURS);
            WorkManager.getInstance().enqueue(builder.build());
        }
    }

}
