package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.foxyourprivacy.privacyRiskInfo.R;

/**
 * This is a Fragment for the settings screen, where your classes and descriptions for permissions and settings can be updated
 * Created by Tim on 25.06.2016.
 */

public class UsageStatsLinkFragment extends Fragment {


    private Button usageStatsSettingsButton;


    /**
     * fills the layout with the permission name and description
     *
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usage_stats_link, container, false);
        Button statsButton = view.findViewById(R.id.button_usage_stats_link);
        usageStatsSettingsButton = statsButton;
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

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
