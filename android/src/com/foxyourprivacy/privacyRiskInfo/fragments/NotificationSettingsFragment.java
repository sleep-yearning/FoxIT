package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;

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



}
