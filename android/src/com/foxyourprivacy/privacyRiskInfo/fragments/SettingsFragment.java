package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.BuildConfig;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;

import java.util.HashMap;

/**
 * This Fragment is the first one that is shown in settings.
 * From here the user can select different options.
 * Created by Hannah on 24.09.2016.
 */
public class SettingsFragment extends ListFragment implements AdapterView.OnItemClickListener {


    private final HashMap<String, Fragment> fragmentList = new HashMap<>();
    private String[] profileListItems;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            profileListItems = new String[]{
                    getString(R.string.textsize),
                    getString(R.string.Impressum),
                    getString(R.string.debugging),
            };


        fragmentList.put(getString(R.string.textsize), new SelectFragment());
        fragmentList.put(getString(R.string.Impressum), new LegalInformationFragment());
        fragmentList.put(getString(R.string.debugging), new CSVRefreshFragment());


        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ValueKeeper v = ValueKeeper.getInstance();
        int versionCodeInt = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        TextView versionCode = view.findViewById(R.id.textViewVersion);
        versionCode.setText(getString(R.string.versionDisplay, versionName, versionCodeInt));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Fragment newPage = fragmentList.get(profileListItems[position]);
            if (newPage != null) {

                //add fragment to the activitys' context
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.add(R.id.second_fragment_frame, newPage, "setting");
                transaction = transaction.addToBackStack("setting");
                transaction.commit();

                RelativeLayout firstFragment = getActivity().findViewById(R.id.first_fragment_frame);
                firstFragment.setVisibility(View.GONE);
            }


    }


    /** defines the listView
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //filling the listView with data
        super.onActivityCreated(savedInstanceState);
        //creates the listView
        ArrayAdapter<String> adapter = new SettingsFragment.MyListAdapter_permission();   //new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,permissionArray);//new MyListAdapter_permission();
        setListAdapter(adapter);
        //on item click an PermissionDescriptionFragment is created. (the OnItemClick() methodLeft)
        getListView().setOnItemClickListener(this);

    }


    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_permission extends ArrayAdapter<String> {
        private MyListAdapter_permission() {
            //defining the listView's layout for single entries
            super(context, R.layout.list_item_profile, profileListItems);
        }

        /**
         * Here ist defined how the XML-Layout is filed  by the data stored in the array.
         *
         * @param position    position in the array used
         * @param convertView
         * @param parent
         * @return
         * @author Tim
         */
        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //convertView has to be filled with layout_app if it's null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.list_item_profile, parent, false);
            }


            TextView fragmentName = itemView.findViewById(R.id.text_fragment_name);
            fragmentName.setText(profileListItems[position]);


            return itemView;
        }


    }

}
