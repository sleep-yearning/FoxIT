package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.SettingsActivity;

/**
 * This is a Fragment for the settings screen, where your classes and descriptions for permissions and settings can be updated
 * Created by Tim on 25.06.2016.
 */

//TODO explain this view a bit more, what do the buttons do?
public class CSVRefreshFragment extends Fragment {


    private Button refreshClassButton;
    private Button refreshDescriptionButton;


    /**fills the layout with the permission name and description
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_csv_refresh, container, false);
        Button classButton = view.findViewById(R.id.button_class_csv);
        refreshClassButton = classButton;
        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshClassButton.setBackgroundColor(Color.GREEN);
                ((SettingsActivity) getActivity()).updateLessions(getActivity(), (ConnectivityManager) getActivity().getSystemService(android.app.Activity.CONNECTIVITY_SERVICE));
                /*
                DBHandler dbHandler = new DBHandler(getActivity(),null,null,1);
                dbHandler.updateLessons(((SettingsActivity)getActivity()).readCSV(R.raw.lektionen,getActivity()));
                dbHandler.updateClasses(((SettingsActivity)getActivity()).readCSV(R.raw.classes,getActivity()));
                dbHandler.close();
                */

            }
        });

        Button descriptionButton = view.findViewById(R.id.button_description_csv);
        refreshDescriptionButton = descriptionButton;
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshDescriptionButton.setBackgroundColor(Color.GREEN);
                ((SettingsActivity) getActivity()).updatePermissions(getActivity(), (ConnectivityManager) getActivity().getSystemService(android.app.Activity.CONNECTIVITY_SERVICE));
                ((SettingsActivity) getActivity()).updateSettings(getActivity(), (ConnectivityManager) getActivity().getSystemService(android.app.Activity.CONNECTIVITY_SERVICE));

                /*
                DBHandler dbHandler = new DBHandler(getActivity(),null,null,1);
                dbHandler.updatePermissions(((SettingsActivity)getActivity()).readCSV(R.raw.permissions,getActivity()));
                dbHandler.close();
                */
            }
        });



        return view;
    }

    /**
     * Enables to pass arguments to the fragment
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {

    }

}
