package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.AnalysisResults;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * presents and structures the user device settings
 */
public class SettingListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    /*
    an array holding all of the displayed settings
     */
    private String[] settingsArray;

    /*
    context of current activity
     */
    private Context context;

    /**
     * shows a setting list
     *
     * @param inflater
     * @param container
     * @param saveInstanceState
     * @return list view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_list, container, false);
    }

    /**
     * defines the specifications of the fragment list view
     *
     * @param savedInstanceState
     * @author Tim
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();

        //creates the listView
        ArrayAdapter<String> adapter = new MyListAdapter_settings();
        setListAdapter(adapter);

        //adds the onClickAction defined by onItemClick()
        getListView().setOnItemClickListener(this);

        // on scrolling down the scrollHint disappears
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (getActivity() instanceof AnalysisResults) {
                    ((AnalysisResults) getActivity()).setScrollMessageVisiblility(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    /**
     * methodLeft to set the fragment's arguments from the outside
     *
     * @param arg Bundle holding an ArrayList calles "permissions" holding an app's permissions
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {
        ArrayList<String> settings = arg.getStringArrayList("settings");
        if (settings == null) Log.d("SettingListFragment", "setting list is null");
        else {
            settingsArray = settings.toArray(new String[settings.size()]);
            Arrays.sort(settingsArray);
        }

    }

    /**
     * show advanced setting options in new fragment
     *
     * @param parent
     * @param view
     * @param position index of the item clicked on in the settings list
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Bundle settingsBundle = new Bundle(); //Bundle to pass arguments to the Fragment
        SettingValueFragment fragment = new SettingValueFragment();
        String settings = settingsArray[position];

        settingsBundle.putString("settingName", settings.substring(0, settings.indexOf("|t1|")));
        settingsBundle.putString("settingValue", settings.substring(settings.indexOf("|t1|") + 4, settings.indexOf("|t2|")));
        settingsBundle.putString("settingOriginalName", "original: " + settings.substring(settings.indexOf("|t2|") + 4, settings.indexOf("|t3|")));
        settingsBundle.putString("settingDescription", settings.substring(settings.indexOf("|t3|") + 4));
        fragment.setArguments(settingsBundle);

        //add fragment so the activitys' context
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.permissionFrame, fragment, "permission");
        transaction = transaction.addToBackStack("permission");
        transaction.commit();

        //make appListFragment and SettingsListFragment invisible after permissionListFragment is created
        FrameLayout settingFrame = getActivity().findViewById(R.id.settingFrame);
        FrameLayout appFrame = getActivity().findViewById(R.id.appFrame);
        settingFrame.setVisibility(View.GONE);
        appFrame.setVisibility(View.GONE);
        AnalysisResults a = (AnalysisResults) getActivity();
        a.mViewPager.setVisibility(View.GONE);
    }

    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_settings extends ArrayAdapter<String> {

        /**
         * defines what layout the listView uses for a single entry
         *
         * @author Tim
         */
        public MyListAdapter_settings() {
            super(context, R.layout.layout_settings, settingsArray);
        }

        /**
         * defines how the data from the array is inserted into XML-layout
         *
         * @param position    position in the array used
         * @param convertView
         * @param parent
         * @return itemView
         * @author Tim
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            //convertView has to be filled with layout_app if null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.layout_settings, parent, false);
            }

            //set the text for setting type
            TextView settingType = itemView.findViewById(R.id.text_setting_type);
            settingType.setText(getString(R.string.settingTypeText, settingsArray[position].substring(0, settingsArray[position].indexOf("|t1|"))));

            //set the text for the setting's setting
            TextView settingSetting = itemView.findViewById(R.id.text_setting_setting);
            String preValue = settingsArray[position].substring(settingsArray[position].indexOf("|t1|") + 4);
            String value = preValue.substring(0, preValue.indexOf("|t2|"));
            if (value.equals("0")) value = "OFF";
            if (value.equals("1")) value = "ON";
            settingSetting.setText(value);
            return itemView;
        }
    }
}

