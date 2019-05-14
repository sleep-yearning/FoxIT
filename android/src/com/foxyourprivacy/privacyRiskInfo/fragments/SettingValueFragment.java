package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;

/**
 * This fragment is used to show values of different settings of the phone.
 * It is called from the setting list of the analysis.
 * Created by Tim on 25.06.2016.
 */
public class SettingValueFragment extends Fragment {
    private String settingName; //the setting described by the fragment
    private String settingValue;
    private String settingOriginalName;
    private String settingDescription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_value, container, false);

        //entering the permissionName into the headline
        TextView settingNameTextView = view.findViewById(R.id.text_setting_name);
        settingNameTextView.setText(settingName);
        TextView settingValueTextView = view.findViewById(R.id.text_setting_value);
        settingValueTextView.setText(settingValue);
        TextView settingDescriptionTextView = view.findViewById(R.id.text_setting_description);
        settingDescriptionTextView.setText(settingDescription);
        TextView settingOriginalTextView = view.findViewById(R.id.text_setting_original);
        settingOriginalTextView.setText(settingOriginalName);
        //TextView settingOriginalNameTextView= (TextView) view.findViewById(R.id.text_setting_original_name);
        // settingOriginalNameTextView.setText(settingOriginalName);

        //links the onClickEvent for returning to PermissionListFragment to the headline
        RelativeLayout button = view.findViewById(R.id.setting_headline_frame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

        settingName = arg.getString("settingName");
        settingValue = arg.getString("settingValue");
        settingOriginalName = arg.getString("settingOriginalName");
        settingDescription = arg.getString("settingDescription");
    }


}
