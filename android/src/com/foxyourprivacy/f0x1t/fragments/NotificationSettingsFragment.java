package com.foxyourprivacy.f0x1t.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.foxyourprivacy.f0x1t.R;
import com.foxyourprivacy.f0x1t.unlockWorker;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
            @Override
            public void onClick(View v) {
                OneTimeWorkRequest compressionWork =
                        new OneTimeWorkRequest.Builder(unlockWorker.class)
                                .build();
                WorkManager.getInstance().enqueue(compressionWork);

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

}
